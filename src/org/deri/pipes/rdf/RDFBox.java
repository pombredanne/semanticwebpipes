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
package org.deri.pipes.rdf;

import java.util.ArrayList;
import java.util.List;

import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Source;
import org.deri.pipes.model.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public abstract class RDFBox implements Operator {
	private transient Logger logger = LoggerFactory.getLogger(RDFBox.class);
	protected transient ExecBuffer buffer;
	protected transient boolean isExecuted=false;
	@XStreamImplicit
	protected List<Source> source = new ArrayList<Source>();
	
	public void stream(ExecBuffer outputBuffer){
		stream(outputBuffer,null);
    }
	public void stream(ExecBuffer outputBuffer,String uri){
		if(this.buffer == null){
			logger.info("Cannot stream - own buffer is null");
			return;
		}
	   if(outputBuffer==null){
		   logger.warn("Cannot stream - output buffer is null");
		   return;
	   }
		if((null!=uri)&&(uri.trim().length()>0)){
			buffer.stream(outputBuffer,uri.trim());
		}else{
			buffer.stream(outputBuffer);
		}
	}
	
	public ExecBuffer getExecBuffer(){
   	 	return buffer;
    }
	
	public final boolean isExecuted(){
	   	return isExecuted;
	}
	
    public String toString(){
    	return buffer.toString(); 
    }
    
	void addSource(Source source){
		this.source.add(source);
	}


}
