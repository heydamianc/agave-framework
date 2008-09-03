						<h3>Recent Activity</h3>
						<ul>
						<#list overview.recentEntries as recentEntry>
							<li><a href="${contextPath}/${recentEntry.uniqueId}">${recentEntry.owner}</a></li>
						</#list>
						</ul>