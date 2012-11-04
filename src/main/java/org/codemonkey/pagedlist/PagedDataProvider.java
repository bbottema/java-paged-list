package org.codemonkey.pagedlist;

import java.util.List;

/**
 * Provides the {@link PagedList} with a means to obtain pages of data. Can return the data itself, the page size to apply and how big the
 * total dataset is (ie. number of rows in a database).
 * 
 * @author Benny Bottema
 * @param <TDto> See {@link #provide(int, Object)}.
 * @param <TQueryParameters> See {@link #provide(int, Object)}.
 */
public interface PagedDataProvider<TDto, TQueryParameters> {
	/**
	 * Returns a subset of the total data set, where the <code>page</code> parameters determines which subset (which page).
	 * 
	 * @param page Determines what subset of data is required.
	 * @param queryParameters The same query parameters as when the list was created, so that the total subset remains consistently the
	 *            same. If these parameters would be different, the page does not actually return 'next' pages as the total dataset would
	 *            have been changed.
	 * @return A list of items, which is a subset of the total set of items in the data set.
	 */
	List<TDto> provide(int page, TQueryParameters queryParameters);

	/**
	 * Returns the total data set's size, using the same querying parameters as the list was created with (also see
	 * {@link #provide(int, Object)}).
	 * 
	 * @param queryParameters See {@link #provide(int, Object)}.
	 * @return The total number of items in the data set.
	 */
	int getDataSize(TQueryParameters queryParameters);

	/**
	 * @return The page size, which determines the sample size of incremental data fetches.
	 */
	int getPageSize();
}