package org.openscience.cdk.itty;

import com.google.wave.api.ProfileServlet;

public class CDKittyProfileServlet extends ProfileServlet {

	private static final long serialVersionUID = -2281733976559068914L;

	@Override  
    public String getRobotAvatarUrl() {
        return "http://chemdevelkit.appspot.com/avatar.png";
    }
      
    @Override
    public String getRobotName() {
            return "CDKitty - The Chemistry Development Kit Google Wave Robot";
    }
    
    @Override
    public String getRobotProfilePageUrl() {
            return "http://github.com/egonw/CDKitty";
    }

}
