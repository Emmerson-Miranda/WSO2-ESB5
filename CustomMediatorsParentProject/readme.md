# Custom mediator Examples
---------------------

## Example 1 - try/catch mediator

This mediator provide the functionality to add try/catch feature to our sequences.

```xml
	<trycatch>
	    <try>
	       <log level="custom">
	          <property name="TESTCUSTOMMEDIATOR" value="tryblock"/>
	       </log>
	       <send/>
	    </try>
	    <catch>
	       <log level="custom">
	          <property name="TESTCUSTOMMEDIATOR" value="catch block"/>
	       </log>
	    </catch>
	</trycatch>
```
 