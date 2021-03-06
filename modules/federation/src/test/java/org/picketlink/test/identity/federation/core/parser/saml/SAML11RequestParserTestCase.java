/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.picketlink.test.identity.federation.core.parser.saml;

import org.junit.Test;
import org.picketlink.common.util.StaxUtil;
import org.picketlink.identity.federation.core.parsers.saml.SAMLParser;
import org.picketlink.identity.federation.core.saml.v1.SAML11Constants;
import org.picketlink.identity.federation.core.saml.v1.writers.SAML11RequestWriter;
import org.picketlink.identity.federation.core.saml.v2.util.XMLTimeUtil;
import org.picketlink.identity.federation.saml.v1.assertion.SAML11ActionType;
import org.picketlink.identity.federation.saml.v1.assertion.SAML11SubjectType;
import org.picketlink.identity.federation.saml.v1.protocol.SAML11AttributeQueryType;
import org.picketlink.identity.federation.saml.v1.protocol.SAML11AuthenticationQueryType;
import org.picketlink.identity.federation.saml.v1.protocol.SAML11AuthorizationDecisionQueryType;
import org.picketlink.identity.federation.saml.v1.protocol.SAML11QueryAbstractType;
import org.picketlink.identity.federation.saml.v1.protocol.SAML11RequestType;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Test SAML 1.1 Request Parsing
 *
 * @author Anil.Saldhana@redhat.com
 * @since Jun 24, 2011
 */
public class SAML11RequestParserTestCase extends AbstractParserTest {
    @Test
    public void testSAML11RequestWithAuthQuery() throws Exception {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        InputStream configStream = tcl.getResourceAsStream("parser/saml1/saml1-request-authquery.xml");

        SAMLParser parser = new SAMLParser();
        SAML11RequestType request = (SAML11RequestType) parser.parse(configStream);
        assertNotNull(request);

        assertEquals(1, request.getMajorVersion());
        assertEquals(1, request.getMinorVersion());
        assertEquals("aaf23196-1773-2113-474a-fe114412ab72", request.getID());
        assertEquals(XMLTimeUtil.parse("2006-07-17T22:26:40Z"), request.getIssueInstant());

        SAML11QueryAbstractType query = request.getQuery();
        assertTrue(query instanceof SAML11AuthenticationQueryType);
        SAML11AuthenticationQueryType attQuery = (SAML11AuthenticationQueryType) query;

        SAML11SubjectType subject = attQuery.getSubject();
        SAML11SubjectType.SAML11SubjectTypeChoice choice = subject.getChoice();
        assertEquals("myusername", choice.getNameID().getValue());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Lets do the writing
        SAML11RequestWriter writer = new SAML11RequestWriter(StaxUtil.getXMLStreamWriter(baos));
        writer.write(request);
        String writtenString = new String(baos.toByteArray());
        System.out.println(writtenString);
        validateSchema(writtenString);
    }

    @Test
    public void testSAML11RequestWithAttributeQuery() throws Exception {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        InputStream configStream = tcl.getResourceAsStream("parser/saml1/saml1-request-attributequery.xml");

        SAMLParser parser = new SAMLParser();
        SAML11RequestType request = (SAML11RequestType) parser.parse(configStream);
        assertNotNull(request);

        assertEquals(1, request.getMajorVersion());
        assertEquals(1, request.getMinorVersion());
        assertEquals("aaf23196-1773-2113-474a-fe114412ab72", request.getID());
        assertEquals(XMLTimeUtil.parse("2006-07-17T22:26:40Z"), request.getIssueInstant());

        SAML11QueryAbstractType query = request.getQuery();
        assertTrue(query instanceof SAML11AttributeQueryType);
        SAML11AttributeQueryType attQuery = (SAML11AttributeQueryType) query;

        SAML11SubjectType subject = attQuery.getSubject();
        SAML11SubjectType.SAML11SubjectTypeChoice choice = subject.getChoice();
        assertEquals("testID", choice.getNameID().getValue());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Lets do the writing
        SAML11RequestWriter writer = new SAML11RequestWriter(StaxUtil.getXMLStreamWriter(baos));
        writer.write(request);
        String writtenString = new String(baos.toByteArray());
        System.out.println(writtenString);
        validateSchema(writtenString);
    }

    @Test
    public void testSAML11RequestWithAuthorizationQuery() throws Exception {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        InputStream configStream = tcl.getResourceAsStream("parser/saml1/saml1-request-authzquery.xml");

        SAMLParser parser = new SAMLParser();
        SAML11RequestType request = (SAML11RequestType) parser.parse(configStream);
        assertNotNull(request);

        assertEquals(1, request.getMajorVersion());
        assertEquals(1, request.getMinorVersion());
        assertEquals("R1234", request.getID());
        assertEquals(XMLTimeUtil.parse("2002-08-05T10:04:15"), request.getIssueInstant());

        SAML11QueryAbstractType query = request.getQuery();
        assertTrue(query instanceof SAML11AuthorizationDecisionQueryType);
        SAML11AuthorizationDecisionQueryType attQuery = (SAML11AuthorizationDecisionQueryType) query;

        SAML11SubjectType subject = attQuery.getSubject();
        SAML11SubjectType.SAML11SubjectTypeChoice choice = subject.getChoice();
        assertEquals("anil@anil.org", choice.getNameID().getValue());
        assertEquals(SAML11Constants.FORMAT_EMAIL_ADDRESS, choice.getNameID().getFormat().toString());
        assertEquals("http://jboss.org", choice.getNameID().getNameQualifier());

        assertEquals("urn:jboss.resource", attQuery.getResource().toString());
        List<SAML11ActionType> actions = attQuery.get();
        assertEquals(1, actions.size());
        SAML11ActionType action = actions.get(0);
        assertEquals("create", action.getValue());
        assertEquals("http://www.jboss.org", action.getNamespace());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Lets do the writing
        SAML11RequestWriter writer = new SAML11RequestWriter(StaxUtil.getXMLStreamWriter(baos));
        writer.write(request);
        String writtenString = new String(baos.toByteArray());
        System.out.println(writtenString);
        validateSchema(writtenString);
    }

    @Test
    public void testSAML11RequestWithAssertionArtifact() throws Exception {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        InputStream configStream = tcl.getResourceAsStream("parser/saml1/saml1-request-assertionartifact.xml");

        SAMLParser parser = new SAMLParser();
        SAML11RequestType request = (SAML11RequestType) parser.parse(configStream);
        assertNotNull(request);

        assertEquals(1, request.getMajorVersion());
        assertEquals(1, request.getMinorVersion());
        assertEquals("rid", request.getID());
        assertEquals(XMLTimeUtil.parse("2002-06-19T17:03:44.022Z"), request.getIssueInstant());

        assertEquals("abcd", request.getAssertionArtifact().get(0));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Lets do the writing
        SAML11RequestWriter writer = new SAML11RequestWriter(StaxUtil.getXMLStreamWriter(baos));
        writer.write(request);
        String writtenString = new String(baos.toByteArray());
        System.out.println(writtenString);
        validateSchema(writtenString);
    }

    @Test
    public void testSAML11RequestWithAssertionIDReference() throws Exception {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        InputStream configStream = tcl.getResourceAsStream("parser/saml1/saml1-request-assertionIDref.xml");

        SAMLParser parser = new SAMLParser();
        SAML11RequestType request = (SAML11RequestType) parser.parse(configStream);
        assertNotNull(request);

        assertEquals(1, request.getMajorVersion());
        assertEquals(1, request.getMinorVersion());
        assertEquals("rid", request.getID());
        assertEquals(XMLTimeUtil.parse("2002-06-19T17:03:44.022Z"), request.getIssueInstant());

        assertEquals("abcd", request.getAssertionIDRef().get(0));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Lets do the writing
        SAML11RequestWriter writer = new SAML11RequestWriter(StaxUtil.getXMLStreamWriter(baos));
        writer.write(request);
        String writtenString = new String(baos.toByteArray());
        System.out.println(writtenString);
        validateSchema(writtenString);
    }
}