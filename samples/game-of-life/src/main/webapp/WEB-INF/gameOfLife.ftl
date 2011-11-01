<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>Conway's Game of Life</title>
    <style type="text/css">
      body {font: 0.8em; font-family: "Trebuchet MS", "Verdana", "Geneva", sans-serif;}
      em {color: #808080;}
      div#content {float: left; display: block; width: 50%;}
      div#container {float: right; text-align: center;}
      table#board {border: 1px solid #c0c0c0;}
      table#board td {background: #f8f8f8; height: 8px; width: 5px;}
      table#board img {border: none;}
      table#board tbody tr td.highlight {background: #ff0000;}
      table#board tbody tr td.alive {background: black}
      div#source {clear:both; border-top: 1px solid #c0c0c0; padding: 5px;}
      div#footer {text-align: center; border-top: 1px solid #ccc; font-size: 90%;}
      div#footer img {border: none;}
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
    <script type="text/javascript">
      var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
      document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
    </script>
    <script type="text/javascript">
      try {
        var pageTracker = _gat._getTracker("UA-10687198-1");
        pageTracker._trackPageview();
      } catch(err) {}
    </script>
  </head>
  <body>
    <div id="content">
      <h1>Conway's Game of Life</h1>
      <p>
        This is an interactive version of John Conway's Game of Life.  
        Basically, this is a cellular automaton which is intended to 
        simulate a universe with a specific ruleset.  The rules for this
        game are that:
      </p>
      <ul>
        <li>Any live cell with fewer than two live neighbours dies, as if by loneliness.</li>
        <li>Any live cell with more than three live neighbours dies, as if by overcrowding.</li>
        <li>Any dead cell with three live neighbors comes to life.</li>
        <li>Any live cell with two or three live neighbors lives on.</li>
      </ul>
      <p>
        Click on individual cells to bring them to life, or load the grid with an initial configuration.  Then,
        click <em>Play</em> under the board to perform the simulation, or <em>Advance</em> to advance one 
        step in the evolutionary process.
      </p>
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
    <div id="source">
      <strong>View the source</strong>: 
        <a href="http://code.google.com/p/agave-web-framework/source/browse/trunk/agave-samples/game-of-life/src/main/java/agave/samples/gameOfLife/web/GameOfLifeHandler.java">Handler</a> | 
        <a href="http://code.google.com/p/agave-web-framework/source/browse/trunk/agave-samples/game-of-life/src/main/webapp/WEB-INF/gameOfLife.ftl">Freemarker Template</a> | 
        <a href="http://code.google.com/p/agave-web-framework/source/browse/trunk/agave-samples/game-of-life/src/main/java/agave/samples/gameOfLife/simulation/">Simulation</a>
    </div>
    <div id="footer">
      <p>&copy; 2009 <a href="http://damiancarrillo.org">Damian Carrillo</a>.  All rights reserved.</p>
      <p><a href="http://code.google.com/p/agave-web-framework/"><img
              src="http://code.google.com/p/agave-web-framework/logo?logo_id=1253032226"
              alt="The Web on Agave"
              title="The Web on Agave" /></a></p>
    </div>
  </body>
</html>
