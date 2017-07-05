package com.zandero.rest.reader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Request body reader interface to implement
 * to read request body and converting request body to given object type
 *
 * Use RestRouter.getReaders().register(...) to register a global reader
 * or use @RequestReader annotation to associate REST with given reader
 */
public interface HttpRequestBodyReader<T> {

	T read(String value, Class<T> type);

	static Type getGenericType(Class<? extends HttpRequestBodyReader> reader) {

		Type[] genericInterfaces = reader.getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {

			if (genericInterface instanceof ParameterizedType) {

				Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
				return genericTypes[0];

				/*for (Type genericType : genericTypes) {
					System.out.println("Generic type: " + genericType);
				}*/
			}
		}

		return null;
	}
}
