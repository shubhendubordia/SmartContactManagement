package com.smart.session;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class MessageRemover {
	
	
	public void removeMessageFromSession() {
		try {
			System.out.println("removing message");
		HttpSession session = 	((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		session.removeAttribute("message");
			
			
		}
		catch(Exception e){
			System.out.println(e);
			
		}
	}

}


