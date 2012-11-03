package org.codemonkey.pagedlist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class PagedListTest {

	private Object testObject;
	private int expectedPage;

	@Test
	public void testPaging1() {
		final int DATASIZE = 9;

		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(new TestablePagedDataProvider1(), testObject);
		assertEquals(pagedList.size(), DATASIZE);

		expectedPage = 0;
		Integer n1 = pagedList.get(0);
		Integer n2 = pagedList.get(1);
		Integer n3 = pagedList.get(2);
		assertEquals((Integer) 1, (Integer) n1);
		assertEquals((Integer) 2, (Integer) n2);
		assertEquals((Integer) 3, (Integer) n3);

		expectedPage = 1;
		Integer n4 = pagedList.get(3);
		Integer n5 = pagedList.get(4);
		Integer n6 = pagedList.get(5);
		assertEquals((Integer) 4, (Integer) n4);
		assertEquals((Integer) 5, (Integer) n5);
		assertEquals((Integer) 6, (Integer) n6);

		expectedPage = 2;
		Integer n7 = pagedList.get(6);
		Integer n8 = pagedList.get(7);
		Integer n9 = pagedList.get(8);
		assertEquals((Integer) 7, (Integer) n7);
		assertEquals((Integer) 8, (Integer) n8);
		assertEquals((Integer) 9, (Integer) n9);
	}

	@Test
	public void testPaging2() {
		final int DATASIZE = 9;
		TestablePagedDataProvider1 testableProvider = new TestablePagedDataProvider1();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, testObject);
		assertEquals(pagedList.size(), DATASIZE);

		expectedPage = 1;
		Integer n4 = pagedList.get(3);
		Integer n5 = pagedList.get(4);
		Integer n6 = pagedList.get(5);
		assertEquals((Integer) 4, (Integer) n4);
		assertEquals((Integer) 5, (Integer) n5);
		assertEquals((Integer) 6, (Integer) n6);

		expectedPage = 2;
		Integer n7 = pagedList.get(6);
		Integer n8 = pagedList.get(7);
		Integer n9 = pagedList.get(8);
		assertEquals((Integer) 7, (Integer) n7);
		assertEquals((Integer) 8, (Integer) n8);
		assertEquals((Integer) 9, (Integer) n9);

		expectedPage = 0;
		Integer n1 = pagedList.get(0);
		Integer n2 = pagedList.get(1);
		Integer n3 = pagedList.get(2);
		assertEquals((Integer) 1, (Integer) n1);
		assertEquals((Integer) 2, (Integer) n2);
		assertEquals((Integer) 3, (Integer) n3);

		assertEquals((Integer) 1, (Integer) testableProvider.page0Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page1Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page2Request);
	}

	@Test
	public void testPaging3() {
		final int DATASIZE = 5;

		TestablePagedDataProvider2 testableProvider = new TestablePagedDataProvider2();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, null);
		assertEquals(pagedList.size(), DATASIZE);

		expectedPage = 0;
		Integer n1 = pagedList.get(0);
		Integer n2 = pagedList.get(1);
		assertEquals((Integer) 1, (Integer) n1);
		assertEquals((Integer) 2, (Integer) n2);

		expectedPage = 1;
		Integer n3 = pagedList.get(2);
		Integer n4 = pagedList.get(3);
		assertEquals((Integer) 3, (Integer) n3);
		assertEquals((Integer) 4, (Integer) n4);

		expectedPage = 2;
		Integer n5 = pagedList.get(4);
		assertEquals((Integer) 5, (Integer) n5);

		expectedPage = -1;
		n1 = pagedList.get(0);
		n2 = pagedList.get(1);
		assertEquals((Integer) 1, (Integer) n1);
		assertEquals((Integer) 2, (Integer) n2);

		assertEquals((Integer) 1, (Integer) testableProvider.page0Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page1Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page2Request);
	}

	@Test
	public void testPaging4() {
		TestablePagedDataProvider3 testableProvider = new TestablePagedDataProvider3();
		PagedList<Integer, Object> pagedList = new PagedList<Integer, Object>(testableProvider, null);

		expectedPage = 0;
		Integer n1 = pagedList.get(0);
		assertEquals((Integer) 1, (Integer) n1);
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		Integer n3 = pagedList.get(2);
		assertEquals((Integer) 3, (Integer) n3);
		assertEquals(5, pagedList.size());

		testableProvider.changedSize = true;

		expectedPage = 0;
		Integer n2 = pagedList.get(1);
		assertEquals((Integer) 2, (Integer) n2);
		// assertEquals(6, pagedList.size());
		assertEquals(5, pagedList.size());

		expectedPage = 1;
		Integer n4 = pagedList.get(3);
		assertEquals((Integer) 4, (Integer) n4);
		// assertEquals(6, pagedList.size());
		assertEquals(5, pagedList.size());

		expectedPage = 2;
		Integer n6 = pagedList.get(5);
		assertEquals((Integer) 6, (Integer) n6);
		assertEquals(6, pagedList.size());

		expectedPage = -1;
		n6 = pagedList.get(5);
		assertEquals((Integer) 6, (Integer) n6);
		assertEquals(6, pagedList.size());

		// assertEquals((Integer) 2, (Integer) testableProvider.page0Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page0Request);
		// assertEquals((Integer) 2, (Integer) testableProvider.page1Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page1Request);
		assertEquals((Integer) 1, (Integer) testableProvider.page2Request);
	}

	private class TestablePagedDataProvider1 implements PagedDataProvider<Integer, Object> {
		public int page0Request = 0;
		public int page1Request = 0;
		public int page2Request = 0;

		public List<Integer> provide(int page, Object queryParameters) {
			assertSame(queryParameters, testObject);
			List<Integer> results = new ArrayList<Integer>();
			switch (page) {
			case 0:
				if (expectedPage != 0) {
					fail("verkeerde page opgevraagd: 0");
				}
				results.add(1);
				results.add(2);
				results.add(3);
				page0Request++;
				break;
			case 1:
				if (expectedPage != 1) {
					fail("verkeerde page opgevraagd: 1");
				}
				results.add(4);
				results.add(5);
				results.add(6);
				page1Request++;
				break;
			case 2:
				if (expectedPage != 2) {
					fail("verkeerde page opgevraagd: 2");
				}
				results.add(7);
				results.add(8);
				results.add(9);
				page2Request++;
				break;
			default:
				fail("ongeldige pagina opgevraagd");
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

	private class TestablePagedDataProvider2 implements PagedDataProvider<Integer, Object> {
		public int page0Request = 0;
		public int page1Request = 0;
		public int page2Request = 0;

		public List<Integer> provide(int page, Object queryParameters) {
			assertNull(queryParameters);
			List<Integer> results = new ArrayList<Integer>();
			switch (page) {
			case 0:
				if (expectedPage != 0) {
					fail("verkeerde page opgevraagd: 0");
				}
				results.add(1);
				results.add(2);
				page0Request++;
				break;
			case 1:
				if (expectedPage != 1) {
					fail("verkeerde page opgevraagd: 1");
				}
				results.add(3);
				results.add(4);
				page1Request++;
				break;
			case 2:
				if (expectedPage != 2) {
					fail("verkeerde page opgevraagd: 2");
				}
				results.add(5);
				page2Request++;
				break;
			default:
				fail("ongeldige pagina opgevraagd");
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

	private class TestablePagedDataProvider3 implements PagedDataProvider<Integer, Object> {
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
					fail("verkeerde page opgevraagd: 0)");
				}
				results.add(1);
				results.add(2);
				page0Request++;
				break;
			case 1:
				if (expectedPage != 1) {
					fail("verkeerde page opgevraagd: 1");
				}
				results.add(3);
				results.add(4);
				page1Request++;
				break;
			case 2:
				if (expectedPage != 2) {
					fail("verkeerde page opgevraagd: 2");
				}
				results.add(5);
				if (changedSize) {
					results.add(6);
				}
				page2Request++;
				break;
			default:
				fail("ongeldige pagina opgevraagd");
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