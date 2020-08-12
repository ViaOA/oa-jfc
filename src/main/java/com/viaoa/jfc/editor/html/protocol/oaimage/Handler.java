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
package com.viaoa.jfc.editor.html.protocol.oaimage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
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


/**
 * 
 * This is used to handle jpg images that jdk cant handle, that OA can
 * 
 * used by OAHTMLTextPane.setFixedSizeBackgroundImage
 */
public class Handler extends com.viaoa.jfc.editor.html.protocol.classpath.Handler { 
    private final ClassLoader classLoader; 

    private static Logger LOG = Logger.getLogger(Handler.class.getName());
    
    public Handler() { 
        this.classLoader = ClassLoader.getSystemClassLoader();
    } 

    
    @Override 
    protected URLConnection openConnection(final URL u) throws IOException {
        LOG.fine("URL="+u);
        
        byte[] bx = null;

        String srcName = u.getPath();
        File f = new File(srcName);
        
        BufferedImage bi = OAImageUtil.loadImage(f);
        if (bi != null) {
            bx = OAImageUtil.convertToBytes(bi);
        }
        
        String query = u.getQuery();
        if (query != null && query.length() > 0) {
            String[] params = query.split("&");

            int w = 0;
            int h = 0;
            for (int i=0; i<params.length; i++) {
                String s = params[i];
                int pos = s.indexOf("=");
                if (pos < 0) continue;
                s = s.substring(0, pos);
                String s2 = params[i].substring(pos+1); 
                if (s.equalsIgnoreCase("w")) {
                   w = OAConv.toInt(s2);
                }
                else if (s.equalsIgnoreCase("h")) {
                    h = OAConv.toInt(s2);
                }
            }
            if (w > 0 || h > 0) {
                bi = OAImageUtil.convertToBufferedImage(bx);
                bi = OAImageUtil.scaleDownToSize(bi, w, h);
                bx = OAImageUtil.convertToBytes(bi);
            }
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
                return guessContentTypeFromName("test.jpg");  // this needs to be the same that is used by OAImageUtil.convertToBytes()
            } 
        };
        
        return uc; 
    }
    
} 


