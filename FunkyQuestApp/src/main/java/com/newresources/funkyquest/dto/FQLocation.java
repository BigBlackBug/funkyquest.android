package com.newresources.funkyquest.dto;

/**
 * Created by BigBlackBug on 2/22/14.
 */
public class FQLocation {

	private Double lat;

	private Double lon;

	private String type;

	private Integer radius;

	private Path path;

	private String name;

	public FQLocation() {
	}

	public Double getLat() {
		return lat;
	}

	public Double getLon() {
		return lon;
	}

	public String getType() {
		return type;
	}

	public Integer getRadius() {
		return radius;
	}

	public Path getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	private static class Path {

		private String color;

		private String fillColor;

		private Double fillOpacity;

		private Path() {
		}

		public String getColor() {
			return color;
		}

		public String getFillColor() {
			return fillColor;
		}

		public Double getFillOpacity() {
			return fillOpacity;
		}
	}
}
