package moneybooker

import static groovyx.net.http.ContentType.URLENC

import javax.annotation.PostConstruct;

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import groovyx.net.http.HTTPBuilder;

class MoneybookerService {
	def httpBuilder
	static transactional = false

	@PostConstruct
	public void setup(){
		httpBuilder=new HTTPBuilder(ConfigurationHolder.config.moneybooker.url)
	}
	def makeRequest(body) {
		httpBuilder.post( path: ConfigurationHolder.config.moneybooker.path, body: body,
				requestContentType: URLENC ) { resp ->
					def cookie = resp.headers["Set-Cookie"]
					def sessionId=cookie.elements.find{
						it.name=="SESSION_ID"
					}
					return sessionId.value
				}
	}
}
