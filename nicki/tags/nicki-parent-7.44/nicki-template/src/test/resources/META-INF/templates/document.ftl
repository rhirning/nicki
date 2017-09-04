<?xml version="1.0" encoding="iso-8859-1" standalone="yes"?>
<simpleTemplate name="document">
	<data><![CDATA[
	<document>
        <styles>
                <style name="right" align="RIGHT"/>
                <style name="left" align="LEFT"/>
                <style name="center" align="CENTER" vertical-align="CENTER"/>
                <style name="bottom" vertical-align="BOTTOM"/>
                <style name="top" vertical-align="TOP"/>
        </styles>
		<pages>
			<#include "include/start-page.ftl">
			<#include "include/page.ftl">
		</pages>
	</document>
	]]></data>
</simpleTemplate>
