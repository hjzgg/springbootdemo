package com.hjzgg.example.springboot.bpp.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 类注解和方法注解都匹配
 */
public class AnnotationClassAndMethodPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut;

    private BeanFactory beanFactory;

    private Set<Class<? extends Annotation>> classAnnotationTypes;

    private Set<Class<? extends Annotation>> methodAnnotationTypes;

    public AnnotationClassAndMethodPointcutAdvisor(List<Class<? extends Annotation>> classAnnotationTypes, List<Class<? extends Annotation>> methodAnnotationTypes, Advice advice) {
        this.classAnnotationTypes = new HashSet<>(classAnnotationTypes);
        this.methodAnnotationTypes = new HashSet<>(methodAnnotationTypes);
        this.advice = advice;
    }

    @PostConstruct
    public void init() {
        this.pointcut = buildPointcut(this.classAnnotationTypes, this.methodAnnotationTypes);
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    /**
     * Set the {@code BeanFactory} to be used when looking up executors by qualifier.
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    private Pointcut buildPointcut(Set<Class<? extends Annotation>> classAnnotationTypes, Set<Class<? extends Annotation>> methodAnnotationTypes) {
        ComposablePointcut classPointcutResult = null;
        if (!CollectionUtils.isEmpty(classAnnotationTypes)) {
            for (Class<? extends Annotation> annotationType : classAnnotationTypes) {
                Pointcut pointcut = new AnnotationMatchingPointcut(annotationType, null);
                if (Objects.isNull(classPointcutResult)) {
                    classPointcutResult = new ComposablePointcut(pointcut);
                } else {
                    classPointcutResult.union(pointcut);
                }
            }
        }

        ComposablePointcut methodPointcutResult = null;
        if (!CollectionUtils.isEmpty(methodAnnotationTypes)) {
            for (Class<? extends Annotation> annotationType : methodAnnotationTypes) {
                Pointcut pointcut = new AnnotationMatchingPointcut(null, annotationType);
                if (Objects.isNull(methodPointcutResult)) {
                    methodPointcutResult = new ComposablePointcut(pointcut);
                } else {
                    methodPointcutResult.union(pointcut);
                }
            }
        }

        if (Objects.nonNull(classPointcutResult) && Objects.isNull(methodPointcutResult)) {
            return classPointcutResult;
        } else if (Objects.nonNull(methodPointcutResult) && Objects.isNull(classPointcutResult)) {
            return methodPointcutResult;
        } else {
            return classPointcutResult.intersection(methodPointcutResult);
        }
    }
}
