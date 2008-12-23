/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package test.ejb.slsbnicmt;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.*;
import java.net.*;

public class SlsbnicmtTestNG {

    private static final String TEST_NAME =
        "ejb-slsbnicmt";
   
    private String strContextRoot="/slsbnicmt";

//    static String result = "";
    String host=System.getProperty("http.host");
    String port=System.getProperty("http.port");
           

    @Test(groups = { "init" })
    public void test1() throws Exception{
        boolean result=false;       
        try{
            result = test("EJBInject");
            Assert.assertEquals(result, true,"Unexpected Results");
        }catch(Exception e){
            e.printStackTrace();
        throw new Exception(e);
        }
    }

    /*
    @Test(dependsOnGroups = { "init.*" })
    public void test2() throws Exception{
        boolean result = false;
        try{
            result = test("InjectLookup");
            Assert.assertEquals(result, true,"Unexpected Results");
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    @Test(dependsOnGroups = { "init.*" })
    public void test3() throws Exception{
        boolean result=false;
        try{
            result = test("JpaRemove");
            Assert.assertEquals(result, true,"Unexpected Results");
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    @Test(dependsOnGroups = { "init.*" })
    public void test4() throws Exception{
        boolean result=false;
        try{
            result = test("JpaVerify");
            Assert.assertEquals(result, true,"Unexpected Results");
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    */
    private boolean test(String c) throws Exception {
        String EXPECTED_RESPONSE = "Test:Pass";
        String TEST_CASE = TEST_NAME + ":" + c;
        boolean result=false;
        String url = "http://" + host + ":" + port + strContextRoot + 
                     "/test?tc=" + c;
        System.out.println("url="+url);

        HttpURLConnection conn = (HttpURLConnection)
            (new URL(url)).openConnection();
        int code = conn.getResponseCode();
        if (code != 200) {
            System.err.println("Unexpected return code: " + code);
        } else {
            InputStream is = conn.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(is));
            String line = null;
	    while ((line = input.readLine()) != null) {
            if (line.contains(EXPECTED_RESPONSE)) {
                result = true;
                break;
            }	    
        }  }
        return result;
    }

    public static void echo(String msg) {
        System.out.println(msg);
    }
}
