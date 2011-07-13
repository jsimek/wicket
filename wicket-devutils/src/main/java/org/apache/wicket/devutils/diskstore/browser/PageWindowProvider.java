/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.devutils.diskstore.browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.devutils.diskstore.DebugDiskDataStore;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.pageStore.PageWindowManager.PageWindow;

/**
 * An {@link IDataProvider} that extracts the information about the stored pages
 */
class PageWindowProvider implements ISortableDataProvider<PageWindowDescription>
{
	private static final int MAX_PAGES_TO_READ = 1000;

	/**
	 * The model that brings the currently selected session id
	 */
	private final IModel<String> sessionId;

	PageWindowProvider(final IModel<String> sessionId)
	{
		this.sessionId = sessionId;
	}

	public Iterator<? extends PageWindowDescription> iterator(int first, int count)
	{
		List<PageWindow> lastPageWindows = getPageWindows();
		List<PageWindow> subList = lastPageWindows.subList(first, first + count);
		List<PageWindowDescription> pageDescriptions = new ArrayList<PageWindowDescription>();
		for (PageWindow pw : subList)
		{
			pageDescriptions.add(new PageWindowDescription(pw, sessionId.getObject()));
		}

		return pageDescriptions.iterator();
	}

	private List<PageWindow> getPageWindows()
	{
		List<PageWindow> lastPageWindows = new ArrayList<PageWindow>();
		if (sessionId != null && sessionId.getObject() != null)
		{
			String sessId = sessionId.getObject();
			DebugDiskDataStore dataStore = DataStoreHelper.getDataStore();
			List<PageWindow> pageWindows = dataStore.getLastPageWindows(sessId, MAX_PAGES_TO_READ);
			lastPageWindows.addAll(pageWindows);
		}
		return lastPageWindows;
	}

	public int size()
	{
		return getPageWindows().size();
	}

	/**
	 * @param description
	 * 
	 *            {@inheritDoc}
	 */
	public IModel<PageWindowDescription> model(PageWindowDescription description)
	{
		return new Model<PageWindowDescription>(description);
	}

	public void detach()
	{
		sessionId.detach();
	}

	/*
	 * No sort state for now. The provider is ISortableDataProvider just because we use
	 * DefaultDataTable
	 */
	public ISortState getSortState()
	{
		return null;
	}

}
