package org.openscience.cdk.itty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.EventType;
import com.google.wave.api.Range;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

@SuppressWarnings("serial")
public class CDKittyServlet extends AbstractRobotServlet {

	private final static String VERSION = "8";
	
	private final static String MWOF_PREFIX = "mwOf:";
	private final static String HTMLOF_PREFIX = "htmlOf:";
	
	private final static String PATTERN_MOL_FORM = "\\w[\\w|\\d]+\\s";
	
	Pattern mwPattern = Pattern.compile(MWOF_PREFIX + PATTERN_MOL_FORM);
	Pattern htmlPattern = Pattern.compile(HTMLOF_PREFIX + PATTERN_MOL_FORM);

	@Override
	public void processEvents(RobotMessageBundle bundle) {
		Wavelet wavelet = bundle.getWavelet();
        
	    if (bundle.wasSelfAdded()) {
	      Blip blip = wavelet.appendBlip();
	      TextView textView = blip.getDocument();
	      textView.appendMarkup("CDKitty v" + VERSION + ".<br /> " +
	      		  "Prefix a molecular formula with a command to gets the property. " +
	    		  "If the formula is not recognized 0.0 will be returned; the property " +
	    		  "is not calculated until a space is given after the formula. " +
	    		  "The available commands:<br /> " +
	    		  MWOF_PREFIX + " calculate the molecular weight;<br /> " +
	    		  HTMLOF_PREFIX + " return the formula as HTML.");
	    }
	            
	    for (Event e: bundle.getEvents()) {
	      if (e.getType() == EventType.BLIP_SUBMITTED ||
	    	  e.getType() == EventType.BLIP_VERSION_CHANGED) {
	    	  Blip blip = e.getBlip();
	    	  if (!blip.getCreator().equals("CDKitty")) {
	    		  TextView textView = blip.getDocument();
	    		  // apply all known commands
	    		  calcMw(textView); // mfOf:
	    		  returnHTML(textView); // htmlOf:
	    	  }
	      }
	    }
	}

	private void calcMw(TextView textView) {
		while (true) {
			Matcher matcher = mwPattern.matcher(textView.getText());
			if (matcher.find()) {
				String match = matcher.group();
				int start = matcher.start();
				int end = matcher.end();

				String formula = match.substring(MWOF_PREFIX.length()).trim();
				if (formula != null && formula.length() > 0) {
					IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(
							formula,
							NoNotificationChemObjectBuilder.getInstance()
					);

					String replacement = "" + AtomContainerManipulator.getNaturalExactMass(
							MolecularFormulaManipulator.getAtomContainer(mf)
					);
					textView.replace(
						new Range(start, end),
						replacement
					);
					textView.setAnnotation(new Range(start, start+replacement.length()), "chem/molForm", formula);
				}
			} else {
				// OK, nothing more found, so return
				return;
			}
		}
	}
	
	private void returnHTML(TextView textView) {
		while (true) {
			Matcher matcher = htmlPattern.matcher(textView.getText());
			if (matcher.find()) {
				String match = matcher.group();
				int start = matcher.start();
				int end = matcher.end();

				String formula = match.substring(HTMLOF_PREFIX.length()).trim();
				if (formula != null && formula.length() > 0) {
					IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(
						formula,
						NoNotificationChemObjectBuilder.getInstance()
					);

					String replacement = MolecularFormulaManipulator.getHTML(mf);
					textView.replace(
						new Range(start, end),
						replacement
					);
					textView.setAnnotation(new Range(start, start+replacement.length()), "chem/molForm", formula);
				}
			} else {
				// OK, nothing more found, so return
				return;
			}
		}
	}
}
