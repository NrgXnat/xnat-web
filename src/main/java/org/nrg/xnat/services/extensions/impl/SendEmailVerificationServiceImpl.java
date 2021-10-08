package org.nrg.xnat.services.extensions.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.mail.services.EmailRequestLogService;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.turbine.utils.AdminUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.SendEmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendEmailVerificationServiceImpl implements SendEmailVerificationService{

	@Autowired
	public SendEmailVerificationServiceImpl(final EmailRequestLogService emailRequestLogService) {
		_emailRequestLogService = emailRequestLogService;
	}

	@Override
	public void sendEmail(UserI user, String email) throws ExceededRequestsException, EmailNotFoundException, InitializationException {
		 if(StringUtils.isNoneBlank(email)){ 
	          try{
	             if (requests.isEmailBlocked(email)){ 
	                // If the user has exceeded the maximum number of requests.
	                throw new ExceededRequestsException("Exceeded maximum number of email requests."); 
	             }
	             // Send email and log request.
	             AdminUtils.sendNewUserVerificationEmail(getXDATUser(email));
	             requests.logEmailRequest(email, new Date ());
	          }
	          catch(ExceededRequestsException e){
	        	  throw new ExceededRequestsException("Exceeded maximum number of email requests.");
	          }
	          catch(EmailNotFoundException e){
	        	  throw new EmailNotFoundException(e.getMessage());
	          }
	          catch(Exception e) { 
	        	  throw new InitializationException("Unable to send Verification Email.");
	          }
	       }
	}

	 
	private UserI getXDATUser(String email) throws Exception {
		List<? extends UserI> users = Users.getUsersByEmail(email);
		// If no user could be found throw EmailNotFoundException
		if (users.size() == 0) {
			throw new EmailNotFoundException("No Such Email Exists.");
		}

		// Otherwise return the first user in the list.
		return users.get(0);
	}

	public class ExceededRequestsException extends Exception {
		public ExceededRequestsException(String msg) {
			super(msg);
		}
	}

	public class EmailNotFoundException extends Exception {
		public EmailNotFoundException(String msg) {
			super(msg);
		}
	}
	
	private final EmailRequestLogService requests = XDAT.getContextService().getBean(EmailRequestLogService.class);

	private final EmailRequestLogService _emailRequestLogService;
}
