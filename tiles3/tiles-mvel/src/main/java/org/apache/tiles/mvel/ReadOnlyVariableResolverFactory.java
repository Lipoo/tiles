package org.apache.tiles.mvel;

import java.util.HashMap;

import org.apache.tiles.context.TilesRequestContextHolder;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.BaseVariableResolverFactory;

public abstract class ReadOnlyVariableResolverFactory extends
        BaseVariableResolverFactory {

    /**
     * The Tiles request holder.
     */
    protected TilesRequestContextHolder requestHolder;

    /**
     * Constructor.
     *
     * @param requestHolder The Tiles request holder.
     * @since 3..0
     */
    public ReadOnlyVariableResolverFactory(TilesRequestContextHolder requestHolder) {
        this.requestHolder = requestHolder;
        variableResolvers = new HashMap<String, VariableResolver>();
    }

    /** {@inheritDoc} */
    public VariableResolver createVariable(String name, Object value) {
        if (nextFactory != null) {
            return nextFactory.createVariable(name, value);
        }
        throw new UnsupportedOperationException("This variable resolver factory is read only");
    }

    /** {@inheritDoc} */
    public VariableResolver createVariable(String name, Object value,
            Class<?> type) {
        variableResolvers = new HashMap<String, VariableResolver>();
        if (nextFactory != null) {
            return nextFactory.createVariable(name, value, type);
        }
        throw new UnsupportedOperationException("This variable resolver factory is read only");
    }

    /** {@inheritDoc} */
    public boolean isResolveable(String name) {
        return isTarget(name) || isNextResolveable(name);
    }

    /** {@inheritDoc} */
    @Override
    public VariableResolver getVariableResolver(String name) {
        if (isResolveable(name)) {
            if (variableResolvers != null && variableResolvers.containsKey(name)) {
                return variableResolvers.get(name);
            } else if (isTarget(name)) {
                VariableResolver variableResolver = createVariableResolver(name);
                variableResolvers.put(name, variableResolver);
                return variableResolver;
            } else if (nextFactory != null) {
                return nextFactory.getVariableResolver(name);
            }
        }

        throw new UnresolveablePropertyException("unable to resolve variable '" + name + "'");
    }

    /**
     * Creates a variable resolver.
     *
     * @param name The name of the property.
     * @return The variable resolver.
     * @since 3.0.0
     */
    public abstract VariableResolver createVariableResolver(String name);

    /**
     * Base variable resolver.
     *
     * @version $Rev$ $Date$
     * @since 3.0.0
     */
    public abstract static class ReadOnlyVariableResolver implements VariableResolver {

        /**
         * The name of the property.
         */
        protected String name;

        /**
         * Constructor.
         *
         * @param name The name of the property.
         * @since 2.2.0
         */
        public ReadOnlyVariableResolver(String name) {
            this.name = name;
        }

        /** {@inheritDoc} */
        public int getFlags() {
            return 0;
        }

        /** {@inheritDoc} */
        public String getName() {
            return name;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public void setStaticType(Class type) {
            // Does nothing for the moment.
        }

        /** {@inheritDoc} */
        public void setValue(Object value) {
            throw new UnsupportedOperationException("This resolver is read-only");
        }
    }
}
