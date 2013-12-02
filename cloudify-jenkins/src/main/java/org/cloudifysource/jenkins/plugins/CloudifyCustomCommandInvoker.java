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

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.cloudifysource.dsl.rest.request.InvokeCustomCommandRequest;
import org.cloudifysource.restclient.RestClient;
import org.cloudifysource.restclient.exceptions.RestClientException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * 
 * invoke cloudify custom command with the flowing parameters accessKey,
 * secretKey , buketname and fileName the command should use this parameter to
 * download file from S3
 * 
 * @author Shadi Massalha
 * 
 */
@SuppressWarnings("unchecked")
public class CloudifyCustomCommandInvoker extends Recorder implements
		Describable<Publisher> {

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	private String url;
	private String service;
	private String application;
	private String command;
	private String user;
	private String password;
	private String apiVersion;
	private String fileName;
	private String accessKey;
	private String secretKey;
	private String bucketName;

	@DataBoundConstructor
	public CloudifyCustomCommandInvoker(String url, String application,
			String service, String commnad, String user, String password,
			String apiVersion, String fileName, String accessKey,
			String secretKey, String bucketName) {
		this.url = url;
		this.application = application;
		this.service = service;
		this.command = commnad;
		this.user = user;
		this.password = password;
		this.apiVersion = apiVersion;
		this.fileName = fileName;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.bucketName = bucketName;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		log(listener.getLogger(),
				"build result in the perform() is: " + build.getResult());

		if (build.getResult() == Result.FAILURE)
			return true;

		log(listener.getLogger(), "invoke command " + command);

		return invokeCustomCommand(listener);
	}

	protected void log(final PrintStream logger, final String message) {
		logger.println(StringUtils.defaultString(getDescriptor()
				.getDisplayName()) + " " + message);
	}

	private boolean invokeCustomCommand(BuildListener listener)
			throws MalformedURLException {
		boolean success = false;
		try {

			URL _url = new URL(getUrl());

			RestClient restClient = new RestClient(_url, user, password,
					apiVersion);

			InvokeCustomCommandRequest request = new InvokeCustomCommandRequest();

			request.setCommandName(command);

			List<String> parameters = new ArrayList<String>();

			parameters.add(accessKey);
			parameters.add(secretKey);
			parameters.add(bucketName);
			parameters.add(fileName);

			request.setParameters(parameters);

			restClient.invokeServiceCommand(application, service, request);

			success = true;

		} catch (RestClientException e) {
			log(listener.getLogger(), String.format(
					"error occur while invoke %s/%s/%s :" + e.getMessage(),
					application, service, command));
		}

		return success;
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

	public String getFileName() {
		return fileName;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getBucketName() {
		return bucketName;
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
			String _path = formData.getString("fileName");

			String _accessKey = formData.getString("accessKey");
			String _secretKey = formData.getString("secretKey");
			String _bucketName = formData.getString("bucketName");

			return new CloudifyCustomCommandInvoker(_url, _application,
					_service, _command, _user, _password, _apiVersion, _path,
					_accessKey, _secretKey, _bucketName);
		}
	}
}
