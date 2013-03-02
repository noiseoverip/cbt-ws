package com.cbt.ws.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.Executor;

import static com.cbt.ws.jooq.tables.Testpackage.TESTPACKAGE;
import com.cbt.ws.mysql.Db;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Test package web service
 * 
 * Test packages to be kept in: /username/testpackage/testpackage_id
 * 
 * @author SauliusAlisauskas 2013-02-25 Initial version
 * 
 */
@Path("/testpackage/")
public class TestPackage {

	private static final String RESOURCE_DIRECTORY = "C://Dev//CBT//ws-store//";
	
	private final Logger mLogger = Logger.getLogger(TestPackage.class);
	/**
	 * 
	 * Add new test package
	 * 
	 * Create in testpackage in database and get it's id
	 * 
	 * Create user folder if needed
	 * 
	 * Create test package folder if needed
	 * 
	 * Store uploaded file
	 * 
	 * Respond with testpackage id
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/add")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("username") String username) {

		String uploadedFileLocation = "c://Users//esauali//Desktop///temp//" + fileDetail.getFileName();

		// save it
		//writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;
		
		mLogger.info("User: " + username + " uploaded file:" + fileDetail);
		
		return Response.status(200).entity(output).build();

	}
	
	@GET
	public Response get() {
		 Executor create = new Executor(Db.getConnection(), SQLDialect.MYSQL);
         Result<Record> result = create.select().from(TESTPACKAGE).fetch();

         for (Record r : result) {
             Long id = r.getValue(TESTPACKAGE.TESTPACKAGE_ID);
             String title = r.getValue(TESTPACKAGE.PATH);
             String description = r.getValue(TESTPACKAGE.METADATA);

             System.out.println("ID: " + id + " path: " + title + " metadata: " + description);
         }
         
		return Response.ok().build();
	}
	
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
