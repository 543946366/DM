/*
 * Copyright (C) 2015 Doug Melton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.imotom.dm.upnp;

import android.text.TextUtils;

import com.imotom.dm.Consts.Consts;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UPnPDevice {

	private String mRawUPnP;
	private String mRawXml;
	private URL mLocation;
	private String mServer;

	private HashMap<String, String> mProperties;
	private String mCachedIconUrl;

	private UPnPDevice() {
	}

	private String getHost() {
		return mLocation.getHost();
	}

	public InetAddress getInetAddress() throws UnknownHostException {
		return InetAddress.getByName(getHost());
	}

	URL getLocation() {
		return mLocation;
	}

	public String getRawUPnP() {
		return mRawUPnP;
	}

	String getRawXml() {
		return mRawXml;
	}

	String getServer() {
		return mServer;
	}

	public String getIconUrl() {
		return mCachedIconUrl;
	}

	private String generateIconUrl() {
		String path = mProperties.get("xml_icon_url");
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		mCachedIconUrl = mLocation.getProtocol() + "://" + mLocation.getHost() + ":" + mLocation.getPort() + "/" + path;
		return mCachedIconUrl;
	}

	private String getDisplayString(){
		return (mProperties.get("xml_model_name") != null && mProperties.get("xml_model_name").length() > 0 ? " " + mProperties.get("xml_model_name") : "") +
				(mProperties.get("xml_model_number") != null && mProperties.get("xml_model_number").length() > 0 ? " " + mProperties.get("xml_model_number").trim() : "");
	}
	String getFriendlyName() {
		return mProperties.get("xml_friendly_name");
	}

	public String getSerialNumber(){
		return mProperties.get("xml_serial_number");
	}

	String getPresentationURL(){
		return mProperties.get("xml_presentation_URL");
	}

	public String getModelNumber(){
		return mProperties.get("xml_model_number");
	}

	String getModelName(){
		return mProperties.get("xml_model_name");
	}

	public String getScrubbedFriendlyName() {
		String friendlyName = mProperties.get("xml_friendly_name");

		// Special case for SONOS: remove the leading ip address from the friendly name
		// "192.168.1.123 - Sonos PLAY:1" => "Sonos PLAY:1"
		if (friendlyName != null && friendlyName.startsWith(getHost() + " - ")) {
			friendlyName = friendlyName.substring(getHost().length() + 3);
		}

		return friendlyName;
	}

	String getDevName(){
		String name = getScrubbedFriendlyName() + "\n(" + getModelNumber() + getSerialNumber() + ")";
		return name;
	}

	////////////////////////////////////////////////////////////////////////////////
	// UPnP Response Parsing
	////////////////////////////////////////////////////////////////////////////////

	static UPnPDevice getInstance(String raw) {
		HashMap<String, String> parsed = parseRaw(raw);
		try {
			UPnPDevice device = new UPnPDevice();
			device.mRawUPnP = raw;
			device.mProperties = parsed;
			device.mLocation = new URL(parsed.get("upnp_location"));
			device.mServer = parsed.get("upnp_server");
			return device;
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static HashMap<String, String> parseRaw(String raw) {
		HashMap<String, String> results = new HashMap<>();
		for (String line : raw.split("\r\n")) {
			int colon = line.indexOf(":");
			if (colon != -1) {
				String key = line.substring(0, colon).trim().toLowerCase();
				String value = line.substring(colon + 1).trim();
				results.put("upnp_" + key, value);
			}
		}
		return results;
	}

	////////////////////////////////////////////////////////////////////////////////
	// UPnP Specification Downloading / Parsing
	////////////////////////////////////////////////////////////////////////////////

	private transient final OkHttpClient mClient = new OkHttpClient();

	public void downloadSpecs() throws Exception {
		Request request = new Request.Builder()
			.url(mLocation)
			.build();

		Response response = mClient.newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new IOException("Unexpected code " + response);
		}

		mRawXml = response.body().string();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(mRawXml));
		Document doc;
		try {
			doc = db.parse(source);
		}
		catch (SAXParseException e) {
			return;
		}
		XPath xPath = XPathFactory.newInstance().newXPath();

		mProperties.put("xml_icon_url", xPath.compile("//icon/url").evaluate(doc));
		generateIconUrl();
		mProperties.put("xml_friendly_name", xPath.compile("//friendlyName").evaluate(doc));
		mProperties.put("xml_serial_number", xPath.compile("//serialNumber").evaluate(doc));
		mProperties.put("xml_presentation_URL", xPath.compile("//presentationURL").evaluate(doc));
		mProperties.put("xml_model_name", xPath.compile("//modelName").evaluate(doc));
		mProperties.put("xml_model_number", xPath.compile("//modelNumber").evaluate(doc));

	}

	//获取设备信息
	String getMyDetailsMsg() {
		StringBuilder sb = new StringBuilder();
			sb.append("设备名:").append(getDisplayString()).append("\n");
			sb.append("别  名:").append(mProperties.get("xml_friendly_name")).append("\n");
			sb.append("序列号:").append(mProperties.get("xml_serial_number")).append("\n");
			//正则表达式获取IP
			String reg = Consts.REG;
			String IP = mProperties.get("xml_presentation_URL");
			sb.append("IP地址:").append(IP.replaceAll(reg, "$1")).append("\n");
		return sb.toString();
	}
}
