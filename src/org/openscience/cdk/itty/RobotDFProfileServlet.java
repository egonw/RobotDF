package org.openscience.cdk.itty;

import com.google.wave.api.ProfileServlet;

public class RobotDFProfileServlet extends ProfileServlet {

	private static final long serialVersionUID = -2281733976559068914L;

	@Override  
    public String getRobotAvatarUrl() {
        return "http://robotdf.appspot.com/avatar.png";
    }
      
    @Override
    public String getRobotName() {
            return "RobotDF - The RDF Google Wave Robot";
    }
    
    @Override
    public String getRobotProfilePageUrl() {
            return "http://github.com/egonw/RobotDF";
    }

}
