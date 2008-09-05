<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Conway's Game of Life</title>
		<style type="text/css">
			body {font: 0.8em; font-family: Georgia, serif;}
			div#content {float: left;}
			div#container {float: right; text-align: center;}
			table#board {border: 1px solid #c0c0c0;}
			table#board td {background: #f8f8f8; height: 8px; width: 5px;}
			table#board img {border: none;}
			table#board tbody tr td.highlight {background: #ff0000;}
			table#board tbody tr td.alive {background: black}
		</style>
		<script type="text/javascript" src="${contextPath}/js/prototype-1.6.0.2.js"></script>
		<script type="text/javascript">
			Event.observe(window, 'load', function(event) {
				$$('td.cell').each(function(element) {
					Event.observe(element, 'mouseover', function(event) {
						element.addClassName('highlight');
					});
					Event.observe(element, 'mouseout', function(event) {
						element.removeClassName('highlight');
					});
					Event.observe(element, 'click', function(event) {
						if (element.hasClassName('alive')) {
							element.removeClassName('alive');
						} else {
							element.addClassName('alive');
							var url = '${contextPath}/toggleState?' + element.id;
							new Ajax.Request(url, {method: 'get'});
						}
					});
				});
				Event.observe($('playStop'), 'click', function(event) {
					if ($('playStop').innerHTML == 'Stop') {
						$('playStop').update('Play');
						window.playExecuter.stop();
					} else {
						$('playStop').update('Stop');
						new PeriodicalExecuter(function(executer) {
							window.playExecuter = executer;
							new Ajax.Request('${contextPath}/play', {
								method: 'get',
								onSuccess: function(transport) {
									var tick = transport.responseText.evalJSON();
									$('tick').update(tick.count);
									tick.aliveToDead.each(function(position) {
										$('y=' + position.row + '&x=' + position.column).removeClassName('alive');
									});
									tick.deadToAlive.each(function(position) {
										$('y=' + position.row + '&x=' + position.column).addClassName('alive');
									});
									if (tick.stable) {
										$('tick').update($('tick').innerHTML + ' - Stable');
										window.playExecuter.stop();
										$('playStop').update('Play');
									}
								}
							});
						}, 0.5);
					}
				});
			});
		</script>
	</head>
	<body>
		<div id="content">
			<h1>Conway's Game of Life</h1>
			<p>Load the grid with an initial configuration:</p>
			<ul>
				<li><a href="${contextPath}/init/blinker">Blinker</a></li>
				<li><a href="${contextPath}/init/toad">Toad</a></li>
				<li><a href="${contextPath}/init/glider">Glider</a></li>
				<li><a href="${contextPath}/init/lightweightSpaceship">Lightweight Spaceship</a></li>
				<li><a href="${contextPath}/init/pulsar">Pulsar</a></li>
				<li><a href="${contextPath}/init/gosperGliderGun">Gosper Glider Gun</a></li>
			</ul>
		</div>
		<div id="container">
			<table id="board">
				<tbody>
					<#list board.grid as row>
					<tr>
						<#list row as col>
						<td id="y=${col.position.row}&amp;x=${col.position.column}" <#if col.alive>class="alive" </#if>class="cell"></td>
						</#list>
					</tr>
					</#list>
				</tbody>
			</table>
			<a href="#" id="playStop">Play</a> | 
			<a href="${contextPath}/advance">Advance</a> | 
			Tick: <span id="tick">${board.tick.count}</span>
		</div>
	</body>
</html>
