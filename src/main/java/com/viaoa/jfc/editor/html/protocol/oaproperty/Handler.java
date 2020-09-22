/*  Copyright 1999 Vince Via vvia@viaoa.com
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.viaoa.jfc.editor.html.protocol.oaproperty;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import com.viaoa.datasource.OASelect;
import com.viaoa.image.OAImageUtil;
import com.viaoa.object.OAObject;
import com.viaoa.object.OAObjectCacheDelegate;
import com.viaoa.util.OAConv;
import com.viaoa.util.OAString;

// see:
//  http://doc.novsu.ac.ru/oreilly/java/exp/ch09_06.htm

/**
 * This is used to register a URL handler for url schema/protocol "oaproperty", to load images from an OAObject property of type byte[].
 * Example: format is "oaproperty://" + className "/" + propertyName + "?" + Id URL url = new
 * URL("oaproperty://com.vetplan.oa.Pet/picture?932"); Note: this expects the property to be of type byte[], which is the "raw" version of
 * image. !!! NOTE !!! must call: "com.viaoa.jfc.editor.html.protocol.classpath.Handler.register()" to have this registered.
 */
public class Handler extends com.viaoa.jfc.editor.html.protocol.classpath.Handler {
	private final ClassLoader classLoader;

	private static Logger LOG = Logger.getLogger(Handler.class.getName());

	public Handler() {
		this.classLoader = ClassLoader.getSystemClassLoader();
	}

	@Override
	protected URLConnection openConnection(final URL u) throws IOException {
		LOG.fine("URL=" + u);
		String className = u.getAuthority();
		String propName = u.getPath();
		String query = u.getQuery();

		if (className == null || className.length() == 0) {
			String s = "className is required, URL=" + u;
			LOG.fine(s);
			throw new IOException(s);
		}
		if (propName == null || propName.length() == 0) {
			String s = "propertyName is required, URL=" + u;
			LOG.fine(s);
			throw new IOException(s);
		}
		propName = OAString.convert(propName, "/", null);

		if (query == null || query.length() == 0) {
			String s = "id is required, URL=" + u;
			LOG.fine(s);
			throw new IOException(s);
		}

		String[] params = query.split("&");
		String id = params[0];

		if (id == null || id.length() == 0) {
			String s = "id is required, URL=" + u;
			LOG.fine(s);
			throw new IOException(s);
		}

		if (id.toLowerCase().startsWith("id=")) {
			if (id.length() == 3) {
				id = "";
			} else {
				id = id.substring(3);
			}
		}

		Class c;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			String s = "class not found for image property, class=" + className;
			LOG.fine(s);
			throw new IOException(s);
		}
		final Class clazz = c;

		LOG.fine("getting image, class=" + className + ", property=" + propName + ", id=" + id);

		OAObject obj;
		obj = (OAObject) OAObjectCacheDelegate.get(c, id);
		if (obj == null) {
			OASelect sel = new OASelect(clazz);
			sel.select("ID = ?", new Object[] { id });
			obj = (OAObject) sel.next();
			sel.cancel();
		}
		if (obj == null) {
			String s = "object not found, url=" + u;
			LOG.fine(s);
			throw new IOException(s);
		}

		byte[] bx;
		try {
			bx = (byte[]) obj.getProperty(propName);
			if (bx == null) {
				throw new IOException("could not read image from property, url=" + u);
			}
		} catch (Exception e) {
			String s = "read image from property error, url=" + u + ", exception=" + e;
			LOG.fine(s);
			throw new IOException(s, e);
		}

		int wMax = 0;
		int hMax = 0;
		for (int i = 0; i < params.length; i++) {
			String s = params[i];
			int pos = s.indexOf("=");
			if (pos < 0) {
				continue;
			}
			s = s.substring(0, pos);
			String s2 = params[i].substring(pos + 1);
			if (s.equalsIgnoreCase("w") || s.equalsIgnoreCase("mw") || s.equalsIgnoreCase("maxw")) {
				wMax = OAConv.toInt(s2);
			}
			if (s.equalsIgnoreCase("h") || s.equalsIgnoreCase("mh") || s.equalsIgnoreCase("maxh")) {
				hMax = OAConv.toInt(s2);
			}
		}
		if (wMax > 0 || hMax > 0) {
			BufferedImage bi = OAImageUtil.convertToBufferedImage(bx);
			bi = OAImageUtil.scaleDownToSize(bi, wMax, hMax);
			bx = OAImageUtil.convertToBytes(bi);
		}

		final byte[] bs = bx;

		URLConnection uc = new URLConnection(u) {
			synchronized public void connect() throws IOException {
			}

			synchronized public InputStream getInputStream() throws IOException {
				ByteArrayInputStream bais = new ByteArrayInputStream(bs);
				return bais;
			}

			public String getContentType() {
				return guessContentTypeFromName("test.jpg"); // this needs to be the same that is used by OAImageUtil.convertToBytes()
			}

		};

		return uc;
	}

}
