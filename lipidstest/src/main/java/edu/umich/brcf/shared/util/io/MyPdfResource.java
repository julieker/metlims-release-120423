////////////////////////////////////////////////////
// MyPDFResource.java
// Written by Jan Wigginton, Jun 17, 2017
////////////////////////////////////////////////////
package edu.umich.brcf.shared.util.io;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.UrlResourceReference;
//import org.apache.wicket.markup.html.image.resource
 
/**
 * @author Ernesto Reinaldo
 *
 */
public class MyPdfResource extends ByteArrayResource {
 
    public MyPdfResource(String contentType)
		{
		super(contentType );
		// TODO Auto-generated constructor stub
		}

	private static final long serialVersionUID = 1L;
 
    static int BUFFER_SIZE = 10*1024;
     
    /**
     *
     */
    /*
   
    public MyPdfResource() { WebResource w;
    }
 
    /* (non-Javadoc)
     * @see org.apache.wicket.markup.html.DynamicWebResource#getResourceState()
     */
    /*
    @Override
    protected ResourceState getResourceState() {
        return new ResourceState() {
             
            @Override
            public String getContentType() {
                return "application/pdf";
            }
             
            @Override
            public byte[] getData() {
                try {
                    return bytes(MyPdfResource.class.getResourceAsStream("test.pdf"));
                } catch (Exception e) {
                    return null;
                }
            }
        }; 
    }  */
     
    public static  byte[] bytes(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(is, out);
        return out.toByteArray();
    }
     
    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        while (true) {
            int tam = is.read(buf);
            if (tam == -1) {
                return;
            }
            os.write(buf, 0, tam);
        }
    }
}