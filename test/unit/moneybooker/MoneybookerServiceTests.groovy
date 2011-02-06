package moneybooker

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import grails.test.*
import groovy.mock.interceptor.MockFor
import groovy.util.Expando;
import groovyx.net.http.HTTPBuilder;

class MoneybookerServiceTests extends GrailsUnitTestCase {
    def moneybookerService
	protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testMakeRequest() {
		configureConfigurationHolder()
		moneybookerService=new MoneybookerService()
		def httpBuilderMock=new MockFor(HTTPBuilder.class)
		httpBuilderMock.demand.post{
			a,b ->
			assertEquals("test_payment.pl",a["path"])
			
			def resp=new DummyResponseWithCookie()
			resp.init()
			def sessionId=b.call(resp)
			
			assertEquals("123123", sessionId)
			
		}
		def mockService=httpBuilderMock.proxyInstance()
		moneybookerService.httpBuilder=mockService
	    moneybookerService.makeRequest()	
		httpBuilderMock.verify mockService
    }
	
	private void configureConfigurationHolder(){
		def expando=new Expando()
		expando.url=""
		expando.path="test_payment.pl"
		ConfigurationHolder.config=["moneybooker":expando]
	}
	
	class DummyResponseWithCookie{
		def headers
		def init(){
			def expando = new Expando()
			expando.name="SESSION_ID"
			expando.value="123123"
			def outerExpando=new Expando()
			outerExpando.elements=[expando]
			headers=["Set-Cookie":outerExpando]
		}
	}
}
