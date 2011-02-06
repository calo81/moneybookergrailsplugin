package moneybooker

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import moneybooker.annotation.MoneybookerParam;

class MoneybookerController {
	
	def moneybookerService

	def index = { }

	def startPayment={PaymentCommand payment ->
		fillMerchantParameters(payment)
		moneybookerService.makeRequest(payment.toMoneyBookerPostBody())
	}
	private fillMerchantParameters(payment){
		payment.merchantEmail=ConfigurationHolder.config.moneybooker.merchant.email
		payment.merchantReturnUrl=ConfigurationHolder.config.moneybooker.merchant.returnUrl
		payment.merchantResultUrl=ConfigurationHolder.config.moneybooker.merchant.resultUrl
	}
}

class PaymentCommand {
	
	@MoneybookerParam("pay_from_email")
	String customerPayMail
	@MoneybookerParam("firstname")
	String customerFirstname
	@MoneybookerParam("lastname")
	String customerSurname
	@MoneybookerParam("amount")
	BigDecimal amount
	@MoneybookerParam("currency")
	String currency
	@MoneybookerParam("amount2_description")
	String description
	@MoneybookerParam("prepare_only")
	boolean prepareOnly=true
	@MoneybookerParam("pay_to_email")
	String merchantEmail
	@MoneybookerParam("return_url")
	String merchantReturnUrl
	@MoneybookerParam("status_url")
	String merchantResultUrl

	def toMoneyBookerPostBody(){
		def stringToPost=new StringBuilder()
		this.properties.each{key,value->
		    def moneyBookerParamName=getMoneyBookerParamName(key)
			if(moneyBookerParamName){
				stringToPost << moneyBookerParamName << "=" << value << "&"
			}
		}
		stringToPost.toString()
	}

	private getMoneyBookerParamName(key){
		try{
			MoneybookerParam annotation =  PaymentCommand.class.getDeclaredField(key).getAnnotation(MoneybookerParam.class)
			return annotation.value()
		}catch(e){
			return ""
		}
	}
	
	static constraints ={
		description(size:1..512,nullable: false)
		customerFirstname(size:1..50,nullable: false)
		customerPayMail(size:1..50,nullable: false)
		customerSurname(size:1..50,nullable: false)
		amount(nullable: false)
		currency(size:3..3,nullable: false)
	}
}
