package moneybooker

import grails.test.*
import groovy.mock.interceptor.MockFor;

class MoneybookerControllerTests extends ControllerUnitTestCase {
   protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testStartPayment() {
		mockCommandObject(PaymentCommand)
		mockConfig ("""
		    moneybooker.merchant.email='carlo.scarioni@gmail.com'
			moneybooker.merchant.returnUrl='/moneybooker/return'
			moneybooker.merchant.resultUrl='/moneybooker/result'
		""")
		def validCommand=createValidCommand()	
		def moneyBookerService = new MockFor(MoneybookerService)
		moneyBookerService.demand.makeRequest{
			body ->
			assertEquals(validCommand.toMoneyBookerPostBody(),body)
			
		}
		def mockMoneyBookderService=moneyBookerService.proxyInstance()
		this.controller.moneybookerService=mockMoneyBookderService
		this.controller.startPayment(validCommand)
		moneyBookerService.verify mockMoneyBookderService
    }
	
	void testInvalidCommandForLackingField(){
		mockCommandObject (PaymentCommand)
		def invalidCommand=createUnvalidCommand()
		invalidCommand.validate()
		assertTrue invalidCommand.hasErrors()
	}

	void testCommandToKeyValueMoneybookerPairString(){
		def command = createValidCommand()
		def streamedCommand = command.toMoneyBookerPostBody()
		
		assertTrue(streamedCommand.contains("pay_from_email=cscarioni@localhost.com"))
		assertTrue(streamedCommand.contains("firstname=carlo"))
		assertTrue(streamedCommand.contains("lastname=scarioni"))
		assertTrue(streamedCommand.contains("currency=EUR"))
		assertTrue(streamedCommand.contains("amount=20.50"))
	}
	
	PaymentCommand createValidCommand(){
		def paymentCommand=new PaymentCommand()
		paymentCommand.customerPayMail="cscarioni@localhost.com"
		paymentCommand.customerFirstname="carlo"
		paymentCommand.customerSurname="scarioni"
		paymentCommand.amount=20.50
		paymentCommand.currency="EUR"
		paymentCommand.description="Nueva computadora"
		paymentCommand
	}
	
	PaymentCommand createUnvalidCommand(){
		def paymentCommand=new PaymentCommand()
		mockForConstraintsTests(PaymentCommand,[paymentCommand])
		paymentCommand.customerPayMail="cscarioni@localhost.com"
		paymentCommand.customerFirstname="carlo"
		paymentCommand.customerSurname="scarioni"
		paymentCommand.amount=20.50
		paymentCommand.currency="EUR"
		paymentCommand
	}
}
