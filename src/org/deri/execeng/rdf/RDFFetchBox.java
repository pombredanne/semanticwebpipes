package org.deri.execeng.rdf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deri.execeng.core.ExecBuffer;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.utils.XMLUtil;
import org.w3c.dom.Element;
import org.openrdf.rio.RDFFormat;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RDFFetchBox extends RDFBox {
	final Logger logger = LoggerFactory.getLogger(RDFFetchBox.class);
	private boolean isExecuted=false;
	private String url=null;
	private RDFFormat format=null;
	private PipeParser parser;
	
	public RDFFetchBox(PipeParser parser,Element element){
		this.parser=parser;
		initialize(element);
	}	
	
	public ExecBuffer getExecBuffer(){
		return buffer;
	}
		
	public boolean isExecuted(){
	   	    return isExecuted;
	}
	
	public void execute(){
		SesameMemoryBuffer rdfBuffer=new SesameMemoryBuffer(parser);
		rdfBuffer.loadFromURL(url,format);			
		buffer=rdfBuffer;
		isExecuted=true;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    
    private void initialize(Element element){
    	url=XMLUtil.getTextFromFirstSubEleByName(element, "location");
    	
    	if((null==url)&&(url.trim().length()==0)){
    		parser.log("Missing location for element "+element);
    	}else{
    		if(null==element.getAttribute("format"))
    			format=RDFFormat.RDFXML;
    		else	
    			format=RDFFormat.valueOf(element.getAttribute("format"));    		
    	}
    	
    	parser.log("Error in RDF fetchbox");
    	parser.log(element.toString());    	
    }
}
