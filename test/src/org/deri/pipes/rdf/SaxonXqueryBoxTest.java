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

import java.io.ByteArrayOutputStream;

import org.deri.pipes.core.Context;
import org.deri.pipes.core.Engine;
import org.deri.pipes.core.ExecBuffer;
import org.deri.pipes.core.Operator;
import org.deri.pipes.core.internals.Source;

import junit.framework.TestCase;

/**
 * @author robful
 *
 */
public class SaxonXqueryBoxTest extends TestCase {
	
	public void test() throws Exception{
		MemoryContextFetcher mcf = new MemoryContextFetcher();
		mcf.setContentType("text/xml");
		mcf.setDefaultValue("xxx");
		mcf.setKey("input");
		String input = createXmlInput();
		Context context = new Context();
		context.put("input", input);
		SaxonXqueryBox xquery = new SaxonXqueryBox();
		xquery.setSource(new Source(mcf));
		xquery.setContentType("text/html");
		xquery.setQuery(createXQuery());
		ExecBuffer result = xquery.execute(context);
		result.stream(System.out);
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		result.stream(bout);
		String answer1 = new String(bout.toByteArray());
		
		String xml = Engine.defaultEngine().serialize(xquery);
		Operator xquery2 = Engine.defaultEngine().parse(xml);
		String xml2 = Engine.defaultEngine().serialize(xquery2);
		assertEquals("wrong xml regenerated",xml,xml2);
		
		ExecBuffer result2 = xquery2.execute(context);
		bout = new ByteArrayOutputStream();
		result2.stream(bout);
		String answer2 = new String(bout.toByteArray());
		
		assertEquals("answers differed",answer1,answer2);

	}

	/**
	 * @return
	 */
	private String createXQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("xquery version \"1.0\";");
		sb.append("\n<html>");
		sb.append("\n<head>");
		sb.append("\n<title>A list of people</title>");
		sb.append("\n    </head>");
		sb.append("\n    <body>");
		sb.append("\n      <h1>A list of people</h1>");
		sb.append("\n      <p>Here are some interesting people:</p>");
		sb.append("\n      <ul> {");
		sb.append("\n        for $b in //persons/person");
		sb.append("\n        order by $b/name return");
		sb.append("\n          <li>{ string($b/name) }</li>");
		sb.append("\n      } </ul>");
		sb.append("\n    </body>");
		sb.append("\n  </html>");
		return sb.toString();
	}

	/**
	 * @return
	 */
	private String createXmlInput() {
		String[] persons = {"Robert Fuller", "Giovanni Tumarello", "Danh Le Phouc"};
		StringBuilder sb = new StringBuilder();
		sb.append("<people><persons>");
		for(String person : persons){
			sb.append("\n\t<person>");
			sb.append("\\n<name>");
			sb.append(person);
			sb.append("</name>");
			sb.append("\n\t</person>");
		}
		sb.append("\n</persons></people>");
		return sb.toString();
	}

}