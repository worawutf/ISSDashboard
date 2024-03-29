package com.iss.dashboard.exception;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.util.Iterator;
import java.util.Map;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {
	 private static final Logger log = Logger.getLogger(CustomExceptionHandler.class.getCanonicalName());
	    private ExceptionHandler wrapped;
	 
	    CustomExceptionHandler(ExceptionHandler exception) {
	        this.wrapped = exception;
	    }
	 
	    @Override
	    public ExceptionHandler getWrapped() {
	        return wrapped;
	    }
	 
	    @Override
	    public void handle() throws FacesException {
	    	  
	        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
	        while (i.hasNext()) {
	            ExceptionQueuedEvent event = i.next();
	            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
	            ExceptionQueuedEventContext eqec = event.getContext();
	            // get the exception from context
	            Throwable t = context.getException();
	 
	            final FacesContext fc = FacesContext.getCurrentInstance();
	            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
	            final NavigationHandler nav = fc.getApplication().getNavigationHandler();
	 
	            //here you do what ever you want with exception
	            try {
	            	if(eqec.getException() instanceof ViewExpiredException) {
	            		 FacesContext contexts = eqec.getContext();
	                     NavigationHandler navHandler = contexts.getApplication().getNavigationHandler();
	                     navHandler.handleNavigation(contexts, null, "login?faces-redirect=true&expired=true");
	            	}else{
		                //log error ?
		                log.log(Level.FATAL, "Critical Exception!", t);
		 
		                //redirect error page
		                requestMap.put("exceptionMessage", t.getMessage());
		                nav.handleNavigation(fc, null, "/error");
		                fc.renderResponse();
	            	}
	            } finally {
	                //remove it from queue
	                i.remove();
	            }
	        }
	        //parent hanle
	        getWrapped().handle();
	    }

}
