package com.acgsior.selector.impl;

import com.acgsior.image.ImageDownloader;
import com.acgsior.image.ImageType;
import com.acgsior.selector.AttributeObjectSelector;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Yove on 16/07/01.
 */
public class ImageObjectSelector extends AttributeObjectSelector {

	private static Logger logger = LoggerFactory.getLogger(ImageObjectSelector.class);

	private boolean synchronize;

	@Autowired
	private ImageDownloader syncDownloader;

	private ImageDownloader asyncDownloader;

	private ImageType imageType;

	@Override
	public String select(final Element element, final Optional parentId) {
		String imageSrc = super.select(element, parentId);
		if (StringUtils.isBlank(imageSrc)) {
			return StringUtils.EMPTY;
		}
		imageSrc = standardizeImageURL(imageSrc);

		String imagePath = StringUtils.EMPTY;
		try {
			if (synchronize) {
				imagePath = syncDownloader.downloadImage(imageType, imageSrc, String.valueOf(parentId.get())).toString();
			} else {
				imagePath = asyncDownloader.downloadImage(imageType, imageSrc, String.valueOf(parentId.get())).toString();
			}
		} catch (IOException e) {
			logger.error(String.format("Failed to download image: %s", imageSrc));
		}
		return imagePath;
	}

	protected String standardizeImageURL(String URL) {
		if (StringUtils.isBlank(URL)) {
			return URL;
		}
		if (!URL.startsWith("http:")) {
			return "http:".concat(URL);
		}
		return URL;
	}

	public void setSynchronize(final boolean synchronize) {
		this.synchronize = synchronize;
	}

	public void setImageType(final ImageType imageType) {
		this.imageType = imageType;
	}
}