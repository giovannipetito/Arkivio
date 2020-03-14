package it.giovanni.kotlin.utils.typekit;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TypekitLayoutInflater extends LayoutInflater {

    private static final String[] CLASS_PREFIX_LIST = {
            "android.widget.",
            "android.webkit."
    };

    private TypekitFactory mTypekitFactory;

    // Reflection Hax
    private boolean mSetPrivateFactory = false;
    private Field mConstructorArgs = null;

    public TypekitLayoutInflater(Context context) {
        super(context);
        initLayoutFactories(false);
    }

    TypekitLayoutInflater(LayoutInflater original, Context newContext, final boolean cloned) {
        super(original, newContext);
        mTypekitFactory = new TypekitFactory();
        initLayoutFactories(cloned);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new TypekitLayoutInflater(this, newContext, true);
    }

    @SuppressLint("ObsoleteSdkInt")
    private void initLayoutFactories(boolean cloned) {
        if (cloned) return;

        // If we are HC+ we get and set Factory2 otherwise we just wrap Factory1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getFactory2() != null && !(getFactory2() instanceof WrapperFactory2)) {
                // Sets both Factory/Factory2
                setFactory2(getFactory2());
            }
        }
        // We can do this as setFactory2 is used for both methods.
        if (getFactory() != null && !(getFactory() instanceof WrapperFactory)) {
            setFactory(getFactory());
        }
    }

    @Override
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        setPrivateFactoryInternal();
        return super.inflate(resource, root, attachToRoot);
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        setPrivateFactoryInternal();
        return super.inflate(parser, root, attachToRoot);
    }

    private void setPrivateFactoryInternal() {
        // Already tried to set the factory.
        if (mSetPrivateFactory) return;
        //if (!hasReflection()) return;
        // Skip if not attached to an activity.
        if (!(getContext() instanceof Factory2)) {
            mSetPrivateFactory = true;
            return;
        }

        final Method setPrivateFactoryMethod = ReflectionUtils.getMethod(LayoutInflater.class, "setPrivateFactory");

        if (setPrivateFactoryMethod != null) {
            ReflectionUtils.invokeMethod(this,
                    setPrivateFactoryMethod,
                    new PrivateWrapperFactory2((Factory2) getContext(), this, mTypekitFactory));
        }
        mSetPrivateFactory = true;
    }

    @Override
    public void setFactory(Factory factory) {
        // Only set our factory and wrap calls to the Factory trying to be set!
        if (!(factory instanceof WrapperFactory)) {
            super.setFactory(new WrapperFactory(factory, this, mTypekitFactory));
        } else {
            super.setFactory(factory);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setFactory2(Factory2 factory2) {
        // Only set our factory and wrap calls to the Factory2 trying to be set!
        if (!(factory2 instanceof WrapperFactory2)) {
            super.setFactory2(new WrapperFactory2(factory2, mTypekitFactory));
        } else {
            super.setFactory2(factory2);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return mTypekitFactory.onViewCreated(
                super.onCreateView(parent, name, attrs), name, parent, getContext(), attrs);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        // This mimics the {@code PhoneLayoutInflater} in the way it tries to inflate the base
        // classes, if this fails its pretty certain the app will fail at this point.
        View view = null;
        for (String prefix : CLASS_PREFIX_LIST) {
            try {
                view = createView(name, prefix, attrs);
            } catch (ClassNotFoundException ignored) {
            }
        }
        // In this case we want to let the base class take a crack
        // at it.
        if (view == null) view = super.onCreateView(name, attrs);

        return mTypekitFactory.onViewCreated(view, name, null, view.getContext(), attrs);
    }

    public View onActivityCreateView(View parent, View view, String name, Context context, AttributeSet attrs) {
        return mTypekitFactory.onViewCreated(createCustomViewInternal(parent, view, name, context, attrs), context, attrs);
    }

    private static class WrapperFactory implements Factory {

        private final Factory mFactory;
        private final TypekitLayoutInflater mInflater;
        private final TypekitFactory mTypekitFactory;

        WrapperFactory(Factory factory, TypekitLayoutInflater inflater, TypekitFactory typekitFactory) {
            this.mFactory = factory;
            this.mInflater = inflater;
            this.mTypekitFactory = typekitFactory;
        }

        @SuppressLint("ObsoleteSdkInt")
        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                return mTypekitFactory.onViewCreated(
                        mInflater.createCustomViewInternal(
                                null, mFactory.onCreateView(name, context, attrs), name, context, attrs
                        ),
                        context, attrs
                );
            }
            return mTypekitFactory.onViewCreated(
                    mFactory.onCreateView(name, context, attrs),
                    name, null, context, attrs
            );
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class WrapperFactory2 implements Factory2 {

        final Factory2 mFactory2;
        final TypekitFactory mTypekitFactory;

        WrapperFactory2(Factory2 mFactory2, TypekitFactory mDecorFactory) {
            this.mFactory2 = mFactory2;
            this.mTypekitFactory = mDecorFactory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return mTypekitFactory.onViewCreated(
                    mFactory2.onCreateView(name, context, attrs),
                    name, null, context, attrs);
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mTypekitFactory.onViewCreated(
                    mFactory2.onCreateView(parent, name, context, attrs),
                    name, parent, context, attrs);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class PrivateWrapperFactory2 extends WrapperFactory2 {

        private final TypekitLayoutInflater mInflater;

        PrivateWrapperFactory2(Factory2 factory2, TypekitLayoutInflater inflater, TypekitFactory typekitFactory) {
            super(factory2, typekitFactory);
            mInflater = inflater;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mTypekitFactory.onViewCreated(
                    mInflater.createCustomViewInternal(parent,
                            mFactory2.onCreateView(parent, name, context, attrs),
                            name, context, attrs
                    ),
                    name, parent, context, attrs
            );
        }
    }

    private View createCustomViewInternal(View parent, View view, String name, Context viewContext, AttributeSet attrs) {

        if (view == null && name.indexOf('.') > -1) {
            if (mConstructorArgs == null)
                mConstructorArgs = ReflectionUtils.getField(LayoutInflater.class, "mConstructorArgs");
            if (mConstructorArgs != null) {
                final Object[] mConstructorArgsArr = (Object[]) ReflectionUtils.getValue(mConstructorArgs, this);
                if (mConstructorArgsArr != null) {
                    final Object lastContext = mConstructorArgsArr[0];
                    mConstructorArgsArr[0] = viewContext;
                    ReflectionUtils.setValue(mConstructorArgs, this, mConstructorArgsArr);
                    try {
                        view = createView(name, null, attrs);
                    } catch (ClassNotFoundException ignored) {
                    } finally {
                        mConstructorArgsArr[0] = lastContext;
                        ReflectionUtils.setValue(mConstructorArgs, this, mConstructorArgsArr);
                    }
                }
            }
        }
        return view;
    }
}