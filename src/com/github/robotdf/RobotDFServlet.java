package com.github.robotdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.EventType;
import com.google.wave.api.Range;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@SuppressWarnings("serial")
public class RobotDFServlet extends AbstractRobotServlet {

	private final static String VERSION = "2";
	
	Pattern pattern = Pattern.compile("owl:sameAs\\[.+\\]");

	@Override
	public void processEvents(RobotMessageBundle bundle) {
		Wavelet wavelet = bundle.getWavelet();
        
	    if (bundle.wasSelfAdded()) {
	      Blip blip = wavelet.appendBlip();
	      TextView textView = blip.getDocument();
	      textView.appendMarkup("RobotDF v" + VERSION + ".<br />");
	    }
	            
	    for (Event e: bundle.getEvents()) {
	      if (e.getType() == EventType.BLIP_SUBMITTED ||
	    	  e.getType() == EventType.BLIP_VERSION_CHANGED) {
	    	  Blip blip = e.getBlip();
	    	  if (!blip.getCreator().equals("RobotDF")) {
	    		  TextView textView = blip.getDocument();
	    		  // apply all known commands
	    		  owlSameAs(textView);
	    	  }
	      }
	    }
	}

	private void owlSameAs(TextView textView) {
		while (true) {
			Matcher matcher = pattern.matcher(textView.getText());
			if (matcher.find()) {
				String match = matcher.group();
				int start = matcher.start();
				int end = matcher.end();

				String replacement = match.substring("owl:sameAs[".length());
				replacement = replacement.substring(0, replacement.length()-1);
				if (replacement != null && replacement.length() > 0) {
					try {
						URL realURL = new URL(replacement);
				        URLConnection connection = realURL.openConnection();
				        connection.setRequestProperty(
				            "Accept",
				            "application/xml, application/rdf+xml"
				        );
				        Model model = ModelFactory.createOntologyModel();
				        model.read(connection.getInputStream(), "", "RDF/XML");
						textView.replace(
							new Range(start, end),
							replacement + "[Triples found: " + model.size() + "]"
						);
//						textView.setAnnotation(new Range(start, start+replacement.length()), "chem/molForm", replacement);
					} catch (MalformedURLException e) {
						textView.replace(
						    new Range(start, end),
						    textView.getText() + "[Bad URL]"
						);
					} catch (IOException e) {
						textView.replace(
							new Range(start, end),
							textView.getText() + "[IO Error: " + e.getMessage() + "]"
						);
					}
				}
			} else {
				// OK, nothing more found, so return
				return;
			}
		}
	}

}
