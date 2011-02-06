package moneybooker

import grails.test.*

class MoneybookerServiceITests extends GroovyTestCase {
	static transactional = false
	MoneybookerService moneybookerService
	protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSendRequestToMoneyBooker() {
		moneybookerService.makeRequest("csdcsdcsdvv")
    }
}
