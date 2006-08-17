package net.sf.antcontrib.net.httpclient;


public class Params {
	public static class Param {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}		
	}
	
	public static class DoubleParam extends Param{
		private double value;

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}
		
	}
	
	public static class BooleanParam extends Param{
		private boolean value;

		public boolean getValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}
		
	}

	public static class IntParam extends Param{
		private int value;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
		
	}

	public static class LongParam extends Param{
		private long value;

		public long getValue() {
			return value;
		}

		public void setValue(long value) {
			this.value = value;
		}
		
	}

	public static class StringParam extends Param{
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
}
