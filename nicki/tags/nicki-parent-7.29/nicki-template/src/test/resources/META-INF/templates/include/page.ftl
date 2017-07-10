<?xml version="1.0" encoding="iso-8859-1" standalone="yes"?>
<simpleTemplate name="document">
	<data><![CDATA[
<page name="sheet 2">
<table x="2" y="3">
	<row>
		<column style="center"><text>Rechtename</text></column>
		<column><text>Beschreibung</text></column>
		<column><text>Wert</text></column>
		<column><text>Wert-Beschreibung</text></column>
		<column><text>Kategorie</text></column>
		<column ><text>Kennung</text></column>
	</row>
	<row>
			<column style="center"><text>SCT008-3-2</text></column>
			<column ><text style="right">Rechts Fahren</text></column>
			<column ><text>true</text></column>
			<column ><text></text></column>
			<column ><text>CAT1</text></column>
			<column ><text>${user!}</text></column>
	</row>
	<row>
			<column style="center"><text>SCT008-3-2</text></column>
			<column ><text style="left">LinksFahren</text></column>
			<column ><text>true</text></column>
			<column ><text></text></column>
			<column ><text>CAT1</text></column>
			<column ><text>${user!}</text></column>
	</row>
	<row>
			<column style="center"><text>SCT008-3-2</text></column>
			<column ><text style="left">LinksFahren</text></column>
			<column ><text>true</text></column>
			<column ><text></text></column>
			<column ><text>=2+3</text></column>
			<column ><text>${user!}</text></column>
	</row>
  </table>
</page>
]]></data>
</simpleTemplate>