/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.deri.pipes.ui;

import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.deri.execeng.utils.IDTool;
import org.deri.execeng.utils.XMLUtil;
import org.integratedmodelling.zk.diagram.components.CustomPort;
import org.integratedmodelling.zk.diagram.components.Port;
import org.integratedmodelling.zk.diagram.components.PortType;
import org.integratedmodelling.zk.diagram.components.ZKNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;
/**
 * @author Danh Le Phuoc, danh.lephuoc@deri.org
 *
 */
public class PipeNode extends ZKNode{  
	final Logger logger = LoggerFactory.getLogger(PipeNode.class);
	
	private static final long serialVersionUID = -1520720934219234911L;
	protected String tagName=null;	
	protected Node srcCode=null;
	public class DeleteListener implements org.zkoss.zk.ui.event.EventListener {
		   PipeNode node;
		   public DeleteListener(PipeNode node){
			   this.node=node;
		   }
		   public void onEvent(Event event) throws UiException {
			     if(!(node instanceof OutPipeNode))
			    	 this.node.detach();
		   }
   }
   
	public class DebugListener implements org.zkoss.zk.ui.event.EventListener {
		   PipeNode node;
		   public DebugListener(PipeNode node){
			   this.node=node;
		   }
		   public void onEvent(Event event) throws UiException {
			     node.debug();
		   }
    }
   protected Window wnd=null;
   
   public PipeNode(int x,int y,int width,int height){
	   super(x,y,width,height);
	   this.canDelete=false;
	   wnd =new Window();
	   appendChild(wnd); 	   
   }
   
   protected void initialize(){
	   
   }
   
   public CustomPort createPort(PortType pType,String position){
	   CustomPort port=new CustomPort(((PipeEditor)getWorkspace()).getPTManager(),pType);
	   port.setPosition(position);
	   port.setPortType("custom");
       addPort(port,0,0);
	   return port;
   }
   
   public CustomPort createPort(PortType pType,int x,int y){
	   CustomPort port=new CustomPort(((PipeEditor)getWorkspace()).getPTManager(),pType);
	   port.setPosition("none");
	   port.setPortType("custom");
       addPort(port,x,y);
	   return port;
   }
   
   public CustomPort createPort(byte pType,String position){
	   return createPort(PipePortType.getPType(pType),position);
   }
   
   public CustomPort createPort(byte pType,int x,int y){
	   return createPort(PipePortType.getPType(pType),x,y);
   }
   
   protected Textbox createBox(int w,int h){
		Textbox box=new Textbox();
		box.setHeight(h+"px");
		box.setWidth(w+"px");
		return box;
   }
   
   protected Node getConnectedCode(Document doc,Textbox txtBox,Port port,boolean config){
	    for(Port p:getWorkspace().getIncomingConnections(port.getUuid()))
			if(p.getParent() instanceof ConnectingOutputNode){			
				if(config)	return ((PipeNode)p.getParent()).getSrcCode(doc,config);
				else doc.createCDATASection(((PipeNode)p.getParent()).getSrcCode(config));
			}
		return doc.createCDATASection(txtBox.getValue().trim());
   }
   
   protected String getConnectedCode(Textbox txtBox,Port port){
	    for(Port p:getWorkspace().getIncomingConnections(port.getUuid()))
			if(p.getParent() instanceof ConnectingOutputNode)			
				return (((PipeNode)p.getParent()).getSrcCode(false));
		return (txtBox.getValue().trim());
   }
   
   protected Node getConnectedCode(Document doc,String tagName, Port port,boolean config){
	   Element elm=doc.createElement(tagName);    	
   	   for(Port p:getWorkspace().getIncomingConnections(port.getUuid())){
   		  elm.appendChild(((PipeNode)p.getParent()).getSrcCode(doc,config));
   		  break;
   	   }
   	   return elm;	
   }
   
   public String generateID(){
	  return IDTool.generateRandomID("");
   }
  
   public void insertInSrcCode(Element parentElm,Port incommingPort,String tagName,boolean config){
		for(Port port:getWorkspace().getIncomingConnections(incommingPort.getUuid())){
			Element outElm=parentElm.getOwnerDocument().createElement(tagName);
			Element node=(Element)((PipeNode)port.getParent()).getSrcCode(parentElm.getOwnerDocument(),config);
			if(node.getParentNode()!=null){
				String refID=generateID();
				node.setAttribute("ID", refID);
				outElm.setAttribute("REFID", refID);
			}
			else
				outElm.appendChild(node);
			parentElm.appendChild(outElm);
		}
  }
  
  public void setPosition(Element elm){
	  elm.setAttribute("x", ""+getX());
	  elm.setAttribute("y", ""+getY());
  }
  
  protected void loadConnectedConfig(Element elm,Port port,Textbox txtbox){	  
		Element linkedElm=XMLUtil.getFirstSubElement(elm);
		String txt;
		if(linkedElm!=null){
			PipeNode linkedNode=PipeNode.loadConfig(linkedElm,(PipeEditor)getWorkspace());
			linkedNode.connectTo(port);
		}else if((txt=XMLUtil.getTextData(elm))!=null){
			if(txt.indexOf("${")>=0){				
				ParameterNode paraNode=((PipeEditor)getWorkspace()).getParameter(txt);
				paraNode.connectTo(port);
			}
			else{
				txtbox.setValue(txt);
			}
		}
  }
  
  public void setToobar(){
	   Caption caption =new Caption();
 	   Toolbarbutton delButton= new Toolbarbutton("","img/del-16x16.png");
 	   delButton.setClass("drag");
 	   delButton.addEventListener("onClick", new DeleteListener(this));
 	   Toolbarbutton debugButton= new Toolbarbutton("","img/debug.jpg");
	   debugButton.setClass("drag");
	   debugButton.addEventListener("onClick", new DebugListener(this));
 	   wnd.appendChild(caption);
 	   caption.appendChild(debugButton);
 	   caption.appendChild(delButton);
   }
   
   public Node getSrcCode(Document doc,boolean config){
	   return srcCode;
   }
   
   public void reset(boolean recursive){
	   srcCode=null;
   }
   
   public void reset(Port inPort,boolean recursive){
		for(Port p:getWorkspace().getIncomingConnections(inPort.getUuid()))		
			if(p.getParent() instanceof PipeNode)
				((PipeNode)p.getParent()).reset(recursive);
   }
   
   public String getSrcCode(boolean config){
	   reset(true);
	   DocumentImpl doc =new DocumentImpl();
	   getSrcCode(doc,config);
	   
	   java.io.StringWriter  strWriter =new java.io.StringWriter(); 
	   try{
			java.util.Properties props = 
			org.apache.xml.serializer.OutputPropertiesFactory.getDefaultMethodProperties(org.apache.xml.serializer.Method.XML);
			org.apache.xml.serializer.Serializer ser = org.apache.xml.serializer.SerializerFactory.getSerializer(props);
			ser.setWriter(strWriter);
			ser.asDOMSerializer().serialize(srcCode!=null?(Element)srcCode:doc.getDocumentElement());
			return strWriter.getBuffer().toString();
	   }	 
	   catch(java.io.IOException e){
			
	   }
	   return null;
   }
   
   public static PipeNode loadConfig(Element elm,PipeEditor wsp){
	   //logger.debug(elm.getTagName());
	   if(elm.getTagName().equalsIgnoreCase("pipe")){    
		   List<Element>  paraElms=XMLUtil.getSubElementByName(
				   								XMLUtil.getFirstSubElementByName(elm, "parameters"),"parameter");
		   for(int i=0;i<paraElms.size();i++){
			   wsp.addParameter((ParameterNode)PipeNode.loadConfig(paraElms.get(i),wsp));
		   }
   		   return PipeNode.loadConfig(XMLUtil.getFirstSubElementByName(elm, "code"),wsp);
	   }
	   
	   if(elm.getTagName().equalsIgnoreCase("code"))
		   return OutPipeNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("rdffetch"))    
   		   return RDFFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("simplemix"))    
	   		return SimpleMixNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("construct"))    
	   		return ConstructNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("for"))    
	   		return ForNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("xslt"))    
	   		return XSLTNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("htmlfetch"))    
	   		return HTMLFetchNode.loadConfig(elm,wsp);
	  
	   if(elm.getTagName().equalsIgnoreCase("xmlfetch"))    
	   		return XMLFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("xslfetch"))    
	   		return XSLFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("parameter"))    
	   		return ParameterNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("patch-executor"))    
	   		return PatchExecutorNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("patch-generator"))    
	   		return PatchGeneratorNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("smoosher"))    
	   		return SmoosherNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("rdfs"))    
	   		return RDFSMixNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("select"))    
	   		return SelectNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("tuplefetch"))    
	   		return TupleQueryResultFetchNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("urlbuilder"))    
	   		return URLBuilderNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("variable"))    
	   		return VariableNode.loadConfig(elm,wsp);
	   
	   if(elm.getTagName().equalsIgnoreCase("sparqlendpoint"))    
	   		return SPARQLEndpointNode.loadConfig(elm,wsp);
	   
	   return null;
   }
   
   public void connectTo(Port port){
	   
   }
   public byte getTypeIdx(){
	   return PipePortType.NONE;
   }
   
   public void debug(){	   
	   ((PipeEditor)getWorkspace()).hotDebug(getSrcCode(false));
   }
}
