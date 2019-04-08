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
package com.viaoa.jfc;

import javax.swing.text.*;

import com.viaoa.object.OAObject;

import java.awt.*;


/**
    Used by OATextField and OATextArea, etc to customize/allow for specific inputs.
*/
public class OAPlainDocument extends PlainDocument {
    private int max;
    private boolean bMaxUsed;
    private boolean bConvertToUpper;
    private boolean bConvertToLower;
    private String validChars, invalidChars;
	private boolean bAllowAll;
    
    public static final int ERROR_MAX_LENGTH = 0;
    public static final int ERROR_INVALID_CHAR = 1;
   
    
    private String outputMask;
    private int type;
    public static final int TYPE_STRING = 0;
    public static final int TYPE_INTEGER = 1;
    public static final int TYPE_FLOAT = 2;
    
    private int maxDigits;
    private int maxDecimalDigits;
   
    
    public void setMaxLength(int x) {
        bMaxUsed = (x >= 0);
        this.max = x;
    }
    public int getMaxLength() {
        return this.max;
    }
    public void setConvertToUpper(boolean b) {
        this.bConvertToUpper = b;
    }
    public boolean getConvertToUpper() {
        return this.bConvertToUpper;
    }

    public void setConvertToLower(boolean b) {
        this.bConvertToLower = b;
    }
    public boolean getConvertToLower() {
        return this.bConvertToLower;
    }
    
    
    public void setValidChars(String s) {
        this.validChars = s;
    }
    public String getValidChars() {
        return this.validChars;
    }
    
    public void setInvalidChars(String s) {
        this.invalidChars = s;
    }
    public String getInvalidChars() {
        return this.invalidChars;
    }
    
    public int getMaxDecimalDigits() {
		return maxDecimalDigits;
	}
	public void setMaxDecimalDigits(int maxDecimalDigits) {
		this.maxDecimalDigits = maxDecimalDigits;
	}
	public int getMaxDigits() {
		return maxDigits;
	}
	public void setMaxDigits(int maxDigits) {
		this.maxDigits = maxDigits;
	}
	public String getOutputMask() {
		return outputMask;
	}
	public void setOutputMask(String outputMask) {
		this.outputMask = outputMask;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public void setAllowAll(boolean b) {
		bAllowAll = b;
	}
	
	public void insertString(int offset, String  str, AttributeSet attr) throws BadLocationException {
        if (bAllowAll || str == null) {
            super.insertString(offset, str, attr);
            return;
        }
                
        int x = str.length();
        int x2 = getLength();
        if (bMaxUsed && (x + x2) > max) {
            int x3 = max - x2;
            if (x3 > 0) {
                str = str.substring(0, x3);
            }
            else str = "";
            x = str.length();
            if (!OAObject.getDebugMode()) {
                handleError(ERROR_MAX_LENGTH);
            }
            //return;
        }

        for (int i=0; i<x; i++) {
            char ch = str.charAt(i);
            if (bConvertToUpper) ch = Character.toUpperCase(ch);
            if (bConvertToLower) ch = Character.toLowerCase(ch);

            if (validChars != null) {
            	if (validChars.indexOf(ch) < 0) {
                    // handleError(ERROR_INVALID_CHAR);
                    //return;
            	    continue;
            	}
            }
            if (invalidChars != null) {
            	if (invalidChars.indexOf(ch) >= 0) {
                    // handleError(ERROR_INVALID_CHAR);
                    //return;
                    continue; // could be a formatting char
            	}
            }

            if (type == TYPE_INTEGER) {
            	if (!Character.isDigit(ch) && ch != ',' && ch != ' ') {
                    // handleError(ERROR_INVALID_CHAR);
                    //return;
                    continue;
            	}
            }
            else if (type == TYPE_FLOAT) {
            	if (!Character.isDigit(ch)) {
            		if (ch != '-' && ch != '.' && ch != ',' && ch != ' ') {
                        // handleError(ERROR_INVALID_CHAR);
                        //return;
                        continue;
            		}
            	}
            }
        }
/*
        if (type == TYPE_INTEGER || type == TYPE_FLOAT) {
        	String s = getText(0, getLength());
        	String s2 = "";
        	if (offset != 0) s2 = s.substring(0, offset);
        	s2 += str;
        	if (offset != getLength())  s2 += s.substring(offset);
        	
        }
*/      
        
        super.insertString(offset, str, attr);
    }
    
    /**
        Currently, beeps. Can be overwritten to display error text.
    */
    public void handleError(int errorType) {
        Toolkit.getDefaultToolkit().beep();
    }
    
}