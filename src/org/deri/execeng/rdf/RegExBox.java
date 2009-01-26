package org.deri.execeng.rdf;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Vector;

import org.deri.execeng.core.BoxParser;
import org.deri.execeng.core.PipeParser;
import org.deri.execeng.model.Stream;
import org.deri.execeng.model.Operator;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.w3c.dom.Element;
import org.deri.execeng.utils.XMLUtil;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class RegExBox extends AbstractMerge{
	 ArrayList<String> types,regexes,replacements;
	 public  RegExBox(PipeParser parser,Element element){
		 this.parser=parser;
		 initialize(element);		 
     }
     
     public org.deri.execeng.core.ExecBuffer getExecBuffer(){
    	 return buffer;
     }
     
     public ArrayList<String> getTypes(){
    	 return types;
     }
     
     public ArrayList<String> getRegexes(){
    	 return regexes;
     }
    
     public ArrayList<String> getReplacements(){
    	 return replacements;
     }
     
     public void execute(){
    	 //merge all input sources to Sesame buffer
    	 SesameMemoryBuffer tmp= new SesameMemoryBuffer(parser);
    	 mergeInputs(tmp);
    	 
    	 buffer = new SesameMemoryBuffer(parser);
    	 try{
			 tmp.getConnection().export(new ReplaceHandler(this));
		 }
		 catch(RDFHandlerException e){
			 parser.log(e);
		 }
		 catch(RepositoryException e){
			 parser.log(e);
		 }
    	     	 
    	 isExecuted=true;
     }   
         
     @Override
     protected void initialize(Element element){
    	super.initialize(element); 
   		ArrayList<Element> ruleEles =XMLUtil.getSubElementByName(
   				                       XMLUtil.getFirstSubElementByName(element, "rules"),"rule");
   		types =new ArrayList<String>();
   		regexes= new ArrayList<String>();
   		replacements= new ArrayList<String>();
   		for(int i=0;i<ruleEles.size();i++){
   			if(ruleEles.get(i).getAttribute("type").equalsIgnoreCase("uri")
   			   ||ruleEles.get(i).getAttribute("type").equalsIgnoreCase("literal")){
   				
   				types.add(ruleEles.get(i).getAttribute("type").toLowerCase());
   				regexes.add(XMLUtil.getTextFromFirstSubEleByName(ruleEles.get(i),"regex"));
   				replacements.add(XMLUtil.getTextFromFirstSubEleByName(ruleEles.get(i),"replacement"));
   			}
   			   
   			else{
   				parser.log("'type' attribute of <rule> tag must be 'uri' or 'literal'");
   			}
   			   
   		}
     } 
     
     public RepositoryConnection getConnection(){
		 return buffer.getConnection();
	 }
     
     public class ReplaceHandler extends org.openrdf.repository.util.RDFInserter{
  		private RegExBox regexBox;
  		public ReplaceHandler(RegExBox regexBox){
  			super(regexBox.getConnection());
  		}
  		

  		public void handleStatement(Statement st)
  		throws RDFHandlerException
  		{
  			Resource sub =st.getSubject();
  			URI pred=st.getPredicate();
  			Value obj=st.getObject();
  			for(int i=0;i<regexBox.getTypes().size();i++){	  			
	  			if(regexBox.getTypes().get(i)=="uri"){
	  				if(sub instanceof URI) sub=replace((URI)sub,i);
	  				pred=replace(pred,i); 
	  				if(obj instanceof URI) obj=replace((URI)obj,i);
	  			}
	  			else
	  				if(obj instanceof Literal) obj=replace((Literal)obj,i);
  			}	
  			super.handleStatement(new StatementImpl(sub,pred,obj));
  		}	
  		
  		public URI replace(URI uri,int i){
  			return new URIImpl(uri.toString().replaceAll(regexBox.getRegexes().get(i), regexBox.getReplacements().get(i)));
  		}
  		
  		public Literal replace(Literal literal,int i){
  			return new LiteralImpl(literal.stringValue().replaceAll(regexBox.getRegexes().get(i), regexBox.getReplacements().get(i)));
  		}
     }
}
