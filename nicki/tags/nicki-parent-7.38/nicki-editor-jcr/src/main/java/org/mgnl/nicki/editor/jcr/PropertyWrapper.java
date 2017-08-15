package org.mgnl.nicki.editor.jcr;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyWrapper {
	private static final Logger LOG = LoggerFactory.getLogger(PropertyWrapper.class);

	private Property property;

	public PropertyWrapper(Property property) {
		super();
		this.property = property;
	}

	public String getName() {
		try {
			return property.getName();
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return null;
	}

	public PROPERTY_TYPE getType() {
		try {
			return PROPERTY_TYPE.of(property.getType());
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return null;
	}

	public String getValue() {
		try {
			return property.getValue().getString();
		} catch (ValueFormatException e) {
			LOG.error("Error", e);
		} catch (IllegalStateException e) {
			LOG.error("Error", e);
		} catch (RepositoryException e) {
			LOG.error("Error", e);
		}
		return null;
	}

	public enum PROPERTY_TYPE {
		STRING(PropertyType.STRING) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		BINARY(PropertyType.BINARY) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		DATE(PropertyType.DATE) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		DOUBLE(PropertyType.DOUBLE) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		LONG(PropertyType.LONG) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		BOOLEAN(PropertyType.BOOLEAN) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		NAME(PropertyType.NAME) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		PATH(PropertyType.PATH) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		REFERENCE(PropertyType.REFERENCE) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		WEAKREFERENCE(PropertyType.WEAKREFERENCE) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		},
		URI(PropertyType.URI) {
			@Override
			void setProperty(Node node, String name, String value)
					throws ValueFormatException, VersionException,
					LockException, ConstraintViolationException,
					RepositoryException {
				node.setProperty(name, value);
			}
		};

		int value;

		public int getValue() {
			return value;
		}

		public static PROPERTY_TYPE of(int value) {
			for (PROPERTY_TYPE p : PROPERTY_TYPE.values()) {
				if (p.value == value) {
					return p;
				}
			}
			return null;
		}

		PROPERTY_TYPE(int value) {
			this.value = value;
		}

		public static String toString(int value) {
			return of(value).toString();
		}

		abstract void setProperty(Node node, String name, String value)
				throws ValueFormatException, VersionException, LockException,
				ConstraintViolationException, RepositoryException;
	}

}
