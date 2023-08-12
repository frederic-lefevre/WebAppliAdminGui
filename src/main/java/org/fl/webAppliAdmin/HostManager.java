/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.webAppliAdmin;

import java.util.List;
import java.util.Vector;

import org.fl.util.AdvancedProperties;

public class HostManager {

	private final Vector<Host> hosts ;
	
	public HostManager(AdvancedProperties props, String baseProperty) {
		
		hosts = new Vector<Host>() ;
		
		List<String> logsProperties = props.getKeysElements(baseProperty);
		for (String lp : logsProperties) {
			String address 	= props.getProperty(baseProperty + lp + ".address") ;
			String appPath	= props.getProperty(baseProperty + lp + ".appPath") ;
			hosts.add(new Host(address, appPath)) ;
		}
	}

	public Vector<Host> getHosts() {
		return hosts;
	}

}
