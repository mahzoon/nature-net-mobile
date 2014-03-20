/**
 * @author Douglas Cavalheiro (doug.cav@ig.com.br)
 */

package org.droidpersistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {

	String tableReference();
	String columnReference() default "_id";
	boolean onDeleteCascade() default false;
	boolean onUpdateCascade() default false;
}
