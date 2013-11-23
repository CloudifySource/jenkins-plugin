package org.cloudifysource.jenkins.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.cloudifysource.dsl.rest.request.InvokeCustomCommandRequest;
import org.cloudifysource.dsl.rest.response.InvokeServiceCommandResponse;
import org.cloudifysource.restclient.RestClient;
import org.cloudifysource.restclient.exceptions.RestClientException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class CloudifyCustomCommandInvoker extends Recorder implements
		Describable<Publisher> {

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	private static final String COMMNAD = "GS_USM_CommandName";
	private static final String FILE_NAME = "GS_USM_Command_Parameters0";
	private static final String FILE_CONTENT = "GS_USM_Command_Parameters1";

	private String url;
	private String service;
	private String application;
	private String command;
	private String user;
	private String password;
	private String apiVersion;
	private String path;
	private String accessKey;
	private String secretKey;
	private String buketName;

	@DataBoundConstructor
	public CloudifyCustomCommandInvoker(String url, String application,
			String service, String commnad, String user, String password,
			String apiVersion, String path, String accessKey, String secretKey,
			String buketName) {
		this.url = url;
		this.application = application;
		this.service = service;
		this.command = commnad;
		this.user = user;
		this.password = password;
		this.apiVersion = apiVersion;
		this.path = path;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.buketName = buketName;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		log(listener.getLogger(),
				"build result in the perform() is: " + build.getResult());

		if (build.getResult() == Result.FAILURE)
			return true;

		log(listener.getLogger(), "detecting " + build.getArtifacts().size()
				+ " artifact for upload.");

		File file = new File(path);

		log(listener.getLogger(), "uploading artifact " + file.getName()
				+ "...");

		String encoded = encodeArtifactAsBase64(file);

		log(listener.getLogger(), "invoke upload ...");

		return invokeCustomCommand(file.getName(), encoded, listener);
	}

	protected void log(final PrintStream logger, final String message) {
		logger.println(StringUtils.defaultString(getDescriptor()
				.getDisplayName()) + " " + message);
	}

	private boolean invokeCustomCommand(String fileName, String encoded,
			BuildListener listener) throws MalformedURLException {
		boolean success = false;
		try {

			URL _url = new URL(getUrl());

			RestClient restClient = new RestClient(_url, user, password,
					apiVersion);

			InvokeCustomCommandRequest request = new InvokeCustomCommandRequest();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(COMMNAD, command);
			parameters.put(FILE_NAME, fileName);
			parameters.put(FILE_CONTENT, encoded);

			// TODO: check this phase
			//request.setParameters(parameters);

			InvokeServiceCommandResponse response = restClient
					.invokeServiceCommand(application, service, request);

			success = true;

		} catch (RestClientException e) {
			log(listener.getLogger(), String.format(
					"error occur while invoke %s/%s/%s :" + e.getMessage(),
					application, service, command));
		}

		return success;
	}

	@SuppressWarnings("resource")
	private String encodeArtifactAsBase64(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		is.close();

		byte[] encoded = Base64.encodeBase64(bytes);

		return new String(encoded);
	}

	public BuildStepMonitor getRequiredMonitorService() {

		return BuildStepMonitor.STEP;
	}

	public String getUrl() {
		return url;
	}

	public String getService() {
		return service;
	}

	public String getApplication() {
		return application;
	}

	public String getCommand() {
		return command;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getPath() {
		return path;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getBuketName() {
		return buketName;
	}

	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {

		public DescriptorImpl(Class<? extends Publisher> clazz) {
			super(clazz);
			load();
		}

		public DescriptorImpl() {
			this(CloudifyCustomCommandInvoker.class);
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		public String getDisplayName() {
			return "Cloudify custom command invoker";
		}

		@Override
		public Publisher newInstance(StaplerRequest req, JSONObject formData)
				throws hudson.model.Descriptor.FormException {

			String _url = formData.getString("url");
			String _application = formData.getString("application");
			String _service = formData.getString("service");
			String _command = formData.getString("command");
			String _user = formData.getString("user");
			String _password = formData.getString("password");
			String _apiVersion = formData.getString("apiVersion");
			String _path = formData.getString("path");

			String _accessKey = formData.getString("accessKey");
			String _secretKey = formData.getString("secretKey");
			String _buketName = formData.getString("buketName");

			return new CloudifyCustomCommandInvoker(_url, _application,
					_service, _command, _user, _password, _apiVersion, _path,
					_accessKey, _secretKey, _buketName);
		}
	}
}
