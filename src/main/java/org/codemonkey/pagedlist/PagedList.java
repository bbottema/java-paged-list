package org.codemonkey.pagedlist;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Java <code>List</code> implementation for managing a list of (remote) paged (lazy loaded) objects.<br />
 * <br />
 * Used in conjunction with {@link PagedDataProvider}. When requesting an object using {@link #get(int)}, this
 * list checks whether the page containing the index is fetched already, if not, the entire page is fetched
 * using the provided paged data provider instance (ie. some Dao).<br />
 * <br />
 * Relies on the page data provider to provide dataset size, page size and pages of data. Also carries an optional
 * query parameter object of a user type.
 * 
 * @param <TDto> The object type of (remotely) persisted objects.
 * @param <QueryParameters> The custom query object type to be passed to the paged data provider instance. 
 *          Could be a parameter object, or a List with param strings for example.
 * @see PagedDataProvider
 * @author Benny Bottema
 */
public class PagedList<TDto, QueryParameters> extends AbstractList<TDto> {

    /**
     * User type object passed in during construction. Used when querying for pages of data. Unchanged between 
     * requests (by this list) to provide consistent paging. Can be <code>null</code>.
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
     * Constructor which accepts a paged data provided, a query object (ideally immutable). Requests the size 
     * of the dataset once from the paged data provider.
     */
    public PagedList(PagedDataProvider<TDto, QueryParameters> pagedDataProvider, QueryParameters queryParameters) {
        this.pagedDataProvider = pagedDataProvider;
        this.queryParameters = queryParameters;
        fetchedPages = new HashMap<Integer, List<TDto>>();
        pageSize = pagedDataProvider.getPageSize();
        this.dataSize = pagedDataProvider.getDataSize(queryParameters);
    }

    /**
     * Checks if the page containing the index has already been fetched, before returing the entry from that 
     * page. If not, it is requested from the paged data provider.
     */
    public TDto get(int index) {
        // check if we need to fetch a new page
        int pageNr = (int) Math.floor(index / pageSize);
        if (fetchedPages.get(pageNr) == null) {
            // check if our data has gone stale
            Integer actualDataSize = pagedDataProvider.getDataSize(queryParameters);
            if (!actualDataSize.equals(dataSize)) {
                fetchedPages.clear();
                dataSize = actualDataSize;
            }
            fetchedPages.put(pageNr, pagedDataProvider.provide(pageNr, queryParameters));
        }
        // return paged TDto
        return fetchedPages.get(pageNr).get(index % pageSize);
    }

    /**
     * Returns the size as determined once in the constructor. This is subject to change in between [i]get()[/i]
     * requests, but not reflected in this paged list. A liability to keep in mind.
     */
    public int size() {
        return dataSize;
    }
}