package org.codemonkey.pagedlist;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Java <code>List</code> implementation for managing a list of (remote) paged (lazy loaded) objects.<br />
 * <br />
 * Used in conjunction with {@link PagedDataProvider}. When requesting an object using {@link #get(int)}, this list checks whether the page
 * containing the index is fetched already, if not, the entire page is fetched using the provided paged data provider instance (ie. some
 * Dao).<br />
 * <br />
 * Relies on the page data provider to provide dataset size, page size and pages of data. Also carries an optional query parameter object of
 * a user type.
 * 
 * @author Benny Bottema
 * @param <TDto> The object type of (remotely) persisted objects.
 * @param <QueryParameters> The custom query object type to be passed to the paged data provider instance. Could be a parameter object, or a
 *            List with param strings for example.
 * @see PagedDataProvider
 */
public class PagedList<TDto, QueryParameters> extends AbstractList<TDto> {

	/**
	 * Flag that governs when a {@link PagedList} verifies staleness of its cached data. If the data set's size changes, the cached pages
	 * become invalid.
	 * 
	 * @author Benny Bottema
	 */
	public static enum DataIntegrityCheckingMode {
		/**
		 * Perform no data integrity checks, assume the data set will never change during the use of the current {@link PagedList}.
		 */
		OFF,
		/**
		 * Test for data set changes every time a new page is fetched.
		 */
		ON_FETCH_PAGE,
		/**
		 * Test for data set changes every time {@link PagedList#get(int)} is being invoked.
		 */
		ON_GET
	}

	/**
	 * @see DataIntegrityCheckingMode
	 */
	private final DataIntegrityCheckingMode dataIntegrityCheckingMode;

	/**
	 * User type object passed in during construction. Used when querying for pages of data. Unchanged between requests (by this list) to
	 * provide consistent paging. Can be <code>null</code>.
	 */
	private final QueryParameters queryParameters;

	/**
	 * The object providing (remotely) fetched data in pages.
	 */
	private final PagedDataProvider<TDto, QueryParameters> pagedDataProvider;

	/**
	 * The backing list with pages of data fetched so far. In effect acts as a cache.
	 */
	private final Map<Integer, List<TDto>> fetchedPages;

	private final int pageSize;

	private int dataSize;

	/**
	 * Constructor which accepts a paged data provided, a query object (ideally immutable). Requests the page size and the size of the
	 * complete data set once from the paged data provider.
	 * 
	 * @param pagedDataProvider Provides the page size and data set size and any future requests for (pages of) data.
	 * @param queryParameters The parameters to use for future data requests. The same query parameters should apply for each data request,
	 *            otherwise the order of pages and indeed the content won't be consistent.
	 * @param dataIntegrityCheckingMode See {@link DataIntegrityCheckingMode}.
	 */
	public PagedList(PagedDataProvider<TDto, QueryParameters> pagedDataProvider, QueryParameters queryParameters,
			DataIntegrityCheckingMode dataIntegrityCheckingMode) {
		this.pagedDataProvider = pagedDataProvider;
		this.queryParameters = queryParameters;
		fetchedPages = new HashMap<Integer, List<TDto>>();
		pageSize = pagedDataProvider.getPageSize();
		this.dataSize = pagedDataProvider.getDataSize(queryParameters);
		this.dataIntegrityCheckingMode = dataIntegrityCheckingMode;
	}

	/**
	 * Refers to {@link #PagedList(PagedDataProvider, Object, DataIntegrityCheckingMode)} with default data integrity checking mode
	 * DataIntegrityCheckingMode# TODO
	 * 
	 * @param pagedDataProvider See {@link #PagedList(PagedDataProvider, Object, DataIntegrityCheckingMode)}.
	 * @param queryParameters See {@link #PagedList(PagedDataProvider, Object, DataIntegrityCheckingMode)}.
	 */
	public PagedList(PagedDataProvider<TDto, QueryParameters> pagedDataProvider, QueryParameters queryParameters) {
		this(pagedDataProvider, queryParameters, DataIntegrityCheckingMode.ON_FETCH_PAGE);
	}

	/**
	 * Checks if the page containing the index has already been fetched, before returning the entry from that page. If not, it is requested
	 * from the paged data provider.
	 * 
	 * @param index See {@link List#get(int)}.
	 * @return See {@link List#get(int)}.
	 */
	public TDto get(int index) {
		// check if we need to fetch a new page
		int pageNr = (int) Math.floor(index / pageSize);
		System.out.println("getting item on index " + index + " (page " + pageNr + ")");
		if (dataIntegrityCheckingMode == DataIntegrityCheckingMode.ON_GET) {
			performDataIntegrityCheck();
		}
		if (fetchedPages.get(pageNr) == null) {
			if (dataIntegrityCheckingMode == DataIntegrityCheckingMode.ON_FETCH_PAGE) {
				performDataIntegrityCheck();
			}
			System.out.println("page not found, fetching page " + pageNr + "...");
			fetchedPages.put(pageNr, pagedDataProvider.provide(pageNr, queryParameters));
		}
		// return paged TDto
		return fetchedPages.get(pageNr).get(index % pageSize);
	}

	/**
	 * check if our data has gone stale.
	 */
	private void performDataIntegrityCheck() {
		Integer actualDataSize = pagedDataProvider.getDataSize(queryParameters);
		if (!actualDataSize.equals(dataSize)) {
			System.out.println("cached data has become stale, clearing cache...");
			fetchedPages.clear();
			dataSize = actualDataSize;
			System.out.println("setting new data size to " + dataSize);
		}
	}

	/**
	 * @return The size as determined once in the constructor. This is subject to change in between [i]get()[/i] requests, but not reflected
	 *         in this paged list. A liability to keep in mind.
	 */
	public int size() {
		return dataSize;
	}
}