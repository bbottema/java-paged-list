package org.codemonkey.pagedlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.codemonkey.pagedlist.PagedList.DataIntegrityCheckingMode;
import org.junit.Test;

/**
 * JUnit test for {@link PagedList}.
 * 
 * @author Benny Bottema
 */
@SuppressWarnings("javadoc")
public class PagedListTest {

	private final Object dummyQueryparameters = "test query parameters";

	/**
	 * Set by the junit tests and verified insize the testable {@link PagedDataProvider} implementations (see further down).
	 */
	private int expectedPage;

	/**
	 * Performs basic tests, such as if the right pages are being request the right number of times (which is all pages, all requested
	 * once).
	 */
	@Test
	public void testBasicPagingWithIncrementalPageOrder() {
		final int DATASIZE = 9;

		TestableBasicPagedDataProvider pagedDataProvider = new TestableBasicPagedDataProvider();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(pagedDataProvider, dummyQueryparameters);

		// check if the list size reflects the size of the total data set
		assertEquals(pagedList.size(), DATASIZE);

		expectedPage = 0;
		int n1 = pagedList.get(0);
		int n2 = pagedList.get(1);
		int n3 = pagedList.get(2);
		assertEquals(1, n1);
		assertEquals(2, n2);
		assertEquals(3, n3);

		expectedPage = 1;
		int n4 = pagedList.get(3);
		int n5 = pagedList.get(4);
		int n6 = pagedList.get(5);
		assertEquals(4, n4);
		assertEquals(5, n5);
		assertEquals(6, n6);

		expectedPage = 2;
		int n7 = pagedList.get(6);
		int n8 = pagedList.get(7);
		int n9 = pagedList.get(8);
		assertEquals(7, n7);
		assertEquals(8, n8);
		assertEquals(9, n9);

		assertEquals(1, pagedDataProvider.page0Request);
		assertEquals(1, pagedDataProvider.page1Request);
		assertEquals(1, pagedDataProvider.page2Request);

		// rerun last data fetching test and verify that the correlated page is still only requested just once.
		expectedPage = 2;
		n7 = pagedList.get(6);
		n8 = pagedList.get(7);
		n9 = pagedList.get(8);
		assertEquals(7, n7);
		assertEquals(8, n8);
		assertEquals(9, n9);
		assertEquals(1, pagedDataProvider.page2Request);
	}

	@Test
	public void testBasicPagingWithRandomPageOrder() {
		final int DATASIZE = 9;
		TestableBasicPagedDataProvider testableProvider = new TestableBasicPagedDataProvider();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, dummyQueryparameters);

		// check if the list size reflects the size of the total data set
		assertEquals(pagedList.size(), DATASIZE);

		expectedPage = 1;
		int n4 = pagedList.get(3);
		int n5 = pagedList.get(4);
		int n6 = pagedList.get(5);
		assertEquals((int) 4, (int) n4);
		assertEquals((int) 5, (int) n5);
		assertEquals((int) 6, (int) n6);

		expectedPage = 2;
		int n7 = pagedList.get(6);
		int n8 = pagedList.get(7);
		int n9 = pagedList.get(8);
		assertEquals(7, n7);
		assertEquals(8, n8);
		assertEquals(9, n9);

		expectedPage = 0;
		int n1 = pagedList.get(0);
		int n2 = pagedList.get(1);
		int n3 = pagedList.get(2);
		assertEquals(1, n1);
		assertEquals(2, n2);
		assertEquals(3, n3);

		assertEquals(1, testableProvider.page0Request);
		assertEquals(1, testableProvider.page1Request);
		assertEquals(1, testableProvider.page2Request);

		// rerun one of the data fetching tests and verify that the correlating page is being requested just once.
		expectedPage = 2;
		n7 = pagedList.get(6);
		n8 = pagedList.get(7);
		n9 = pagedList.get(8);
		assertEquals(7, n7);
		assertEquals(8, n8);
		assertEquals(9, n9);
	}

	@Test
	public void testBasicPagingWithNullParameters() {
		final int DATASIZE = 5;

		TestableNullParametersUnevenPageContentDataProvider testableProvider = new TestableNullParametersUnevenPageContentDataProvider();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, null);
		assertEquals(pagedList.size(), DATASIZE);

		expectedPage = 0;
		int n1 = pagedList.get(0);
		int n2 = pagedList.get(1);
		assertEquals(1, n1);
		assertEquals(2, n2);

		expectedPage = 1;
		int n3 = pagedList.get(2);
		int n4 = pagedList.get(3);
		assertEquals(3, n3);
		assertEquals(4, n4);

		expectedPage = 2;
		int n5 = pagedList.get(4);
		assertEquals(5, n5);

		// provider should not be called again for this data (if it is it will fail, because expectedPage should be 0, 1 or 2)
		expectedPage = -1;
		n1 = pagedList.get(0);
		n2 = pagedList.get(1);
		assertEquals(1, n1);
		assertEquals(2, n2);

		assertEquals(1, testableProvider.page0Request);
		assertEquals(1, testableProvider.page1Request);
		assertEquals(1, testableProvider.page2Request);
	}

	@Test
	public void testAdvancedPagingWithChangedDataSet() {
		TestableNullParametersChangingContentProvider testableProvider = new TestableNullParametersChangingContentProvider();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, null);

		expectedPage = 0;
		int n1 = pagedList.get(0);
		assertEquals(1, n1);
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		int n3 = pagedList.get(2);
		assertEquals(3, n3);
		assertEquals(5, pagedList.size());

		// the testable provider will now return a different data set size
		testableProvider.changedSize = true;

		// but the list should not check whether its cached data has become stale and it should not reset
		expectedPage = 0;
		int n2 = pagedList.get(1);
		assertEquals(2, n2);
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		int n4 = pagedList.get(3);
		assertEquals(4, n4);
		assertEquals(5, pagedList.size());

		expectedPage = 2;
		int n6 = pagedList.get(5);
		assertEquals(6, n6);
		assertEquals(6, pagedList.size());

		expectedPage = -1;
		n6 = pagedList.get(5);
		assertEquals(6, n6);
		assertEquals(6, pagedList.size());

		assertEquals(1, testableProvider.page0Request);
		assertEquals(1, testableProvider.page1Request);
		assertEquals(1, testableProvider.page2Request);
	}

	@Test
	public void testAdvancedPagingWithChangedDataSetAndCheckDataIntegrityOnFetchPage() {
		TestableNullParametersChangingContentProvider testableProvider = new TestableNullParametersChangingContentProvider();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, null, DataIntegrityCheckingMode.ON_FETCH_PAGE);

		expectedPage = 0;
		int n1 = pagedList.get(0);
		assertEquals(1, n1);
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		int n3 = pagedList.get(2);
		assertEquals(3, n3);
		assertEquals(5, pagedList.size());

		// the testable provider will now return a different data set size
		testableProvider.changedSize = true;

		// but the list should not check whether its cached data has become stale for page 0 and 1 and it should not reset
		expectedPage = 0;
		int n2 = pagedList.get(1);
		assertEquals(2, n2);
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		int n4 = pagedList.get(3);
		assertEquals(4, n4);
		assertEquals(5, pagedList.size());

		// the list should now recognize its cached data has become stale for page 2 and it should reset
		expectedPage = 2;
		int n6 = pagedList.get(5);
		assertEquals(6, n6);
		assertEquals(6, pagedList.size());

		expectedPage = -1;
		n6 = pagedList.get(5);
		assertEquals(6, n6);
		assertEquals(6, pagedList.size());

		assertEquals(1, testableProvider.page0Request);
		assertEquals(1, testableProvider.page1Request);
		assertEquals(1, testableProvider.page2Request);
	}

	@Test
	public void testAdvancedPagingWithChangedDataSetAndCheckDataIntegrityOnGet() {
		TestableNullParametersChangingContentProvider testableProvider = new TestableNullParametersChangingContentProvider();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, null, DataIntegrityCheckingMode.ON_GET);

		expectedPage = 0;
		int n1 = pagedList.get(0);
		assertEquals(1, n1);
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		int n3 = pagedList.get(2);
		assertEquals(3, n3);
		assertEquals(5, pagedList.size());

		// the testable provider will now return a different data set size
		testableProvider.changedSize = true;

		// the list should now recognize its cached data has become stale and it should reset
		expectedPage = 0;
		int n2 = pagedList.get(1);
		assertEquals(2, n2);
		assertEquals(6, pagedList.size());

		expectedPage = 1;
		int n4 = pagedList.get(3);
		assertEquals(4, n4);
		assertEquals(6, pagedList.size());

		expectedPage = 2;
		int n6 = pagedList.get(5);
		assertEquals(6, n6);
		assertEquals(6, pagedList.size());

		expectedPage = -1;
		n6 = pagedList.get(5);
		assertEquals(6, n6);
		assertEquals(6, pagedList.size());

		assertEquals(2, testableProvider.page0Request);
		assertEquals(2, testableProvider.page1Request);
		assertEquals(1, testableProvider.page2Request);
	}

	/**
	 * <p>
	 * Basic provider which returns a fixed list of numbers ranging from 1 to 9 distributed over 3 pages of 3 items (page size 3). Verifies
	 * that the right <code>queryParameters</code> has been passed into {@link #provide(int, Object)}.
	 * <p>
	 * Will also verify if the right page was requested (set by the junit test itself) and will also throw exception if a non-existing page
	 * is being requested.
	 * <p>
	 * Finally, it will also keep track of all the pages requests, so that the junit test can verify how many times each page was requested
	 * (which should be once only, unless the data set changed in size and a cache-reset is triggered).
	 * 
	 * @author Benny Bottema
	 */
	private class TestableBasicPagedDataProvider implements PagedDataProvider<Integer, Object> {
		public int page0Request = 0;
		public int page1Request = 0;
		public int page2Request = 0;

		public List<Integer> provide(int page, Object queryParameters) {
			assertSame(queryParameters, dummyQueryparameters);
			List<Integer> results = new ArrayList<Integer>();
			switch (page) {
			case 0:
				if (expectedPage != 0) {
					fail("wrong page requestes: 0");
				}
				results.add(1);
				results.add(2);
				results.add(3);
				page0Request++;
				break;
			case 1:
				if (expectedPage != 1) {
					fail("wrong page requestes: 1");
				}
				results.add(4);
				results.add(5);
				results.add(6);
				page1Request++;
				break;
			case 2:
				if (expectedPage != 2) {
					fail("wrong page requestes: 2");
				}
				results.add(7);
				results.add(8);
				results.add(9);
				page2Request++;
				break;
			default:
				fail("invalid page requested");
			}
			return results;
		}

		public int getDataSize(Object queryParameters) {
			return 9;
		}

		public int getPageSize() {
			return 3;
		}
	}

	/**
	 * Same as {@link TestableBasicPagedDataProvider}, but with numbers ranging from 1 to 5 over 3 pages (page size 2) and verifies that the
	 * <code>queryParameters</code> passed in {@link #provide(int, Object)} is <code>null</code>.
	 * 
	 * @author Benny Bottema
	 */
	private class TestableNullParametersUnevenPageContentDataProvider implements PagedDataProvider<Integer, Object> {
		public int page0Request = 0;
		public int page1Request = 0;
		public int page2Request = 0;

		public List<Integer> provide(int page, Object queryParameters) {
			assertNull(queryParameters);
			List<Integer> results = new ArrayList<Integer>();
			switch (page) {
			case 0:
				if (expectedPage != 0) {
					fail("wrong page requestes: 0");
				}
				results.add(1);
				results.add(2);
				page0Request++;
				break;
			case 1:
				if (expectedPage != 1) {
					fail("wrong page requestes: 1");
				}
				results.add(3);
				results.add(4);
				page1Request++;
				break;
			case 2:
				if (expectedPage != 2) {
					fail("wrong page requestes: 2");
				}
				results.add(5);
				page2Request++;
				break;
			default:
				fail("invalid page requested");
			}
			return results;
		}

		public int getDataSize(Object queryParameters) {
			return 5;
		}

		public int getPageSize() {
			return 2;
		}
	}

	/**
	 * The same as {@link TestableNullParametersUnevenPageContentDataProvider}, but when instructed by the junit test, will add a sixth item
	 * and so change the set of data. When already fetched, the {@link PagedList} should clear it's page cache and again fetch all pages
	 * (when required).
	 * 
	 * @author Benny Bottema
	 */
	private class TestableNullParametersChangingContentProvider implements PagedDataProvider<Integer, Object> {
		public int page0Request = 0;
		public int page1Request = 0;
		public int page2Request = 0;

		private boolean changedSize = false;

		public List<Integer> provide(int page, Object queryParameters) {
			assertNull(queryParameters);
			List<Integer> results = new ArrayList<Integer>();
			switch (page) {
			case 0:
				if (expectedPage != 0) {
					fail("wrong page requestes: 0)");
				}
				results.add(1);
				results.add(2);
				page0Request++;
				break;
			case 1:
				if (expectedPage != 1) {
					fail("wrong page requestes: 1");
				}
				results.add(3);
				results.add(4);
				page1Request++;
				break;
			case 2:
				if (expectedPage != 2) {
					fail("wrong page requestes: 2");
				}
				results.add(5);
				if (changedSize) {
					results.add(6);
				}
				page2Request++;
				break;
			default:
				fail("invalid page requested");
			}
			return results;
		}

		public int getDataSize(Object queryParameters) {
			return (changedSize) ? 6 : 5;
		}

		public int getPageSize() {
			return 2;
		}
	}
}