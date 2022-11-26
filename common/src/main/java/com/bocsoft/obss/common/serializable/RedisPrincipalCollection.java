package com.bocsoft.obss.common.serializable;

import lombok.*;
import org.apache.shiro.subject.MutablePrincipalCollection;
import org.apache.shiro.subject.PrincipalCollection;

import java.beans.Transient;
import java.util.*;

/**
 * 序列化principalCollection，避免principalCollection存redis无法反序列化问题
 * 改造SimplePrincipalCollection，去除transient修饰
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class RedisPrincipalCollection implements MutablePrincipalCollection {

    private Map<String, Set> realmPrincipals;

    public RedisPrincipalCollection(Object principal, String realmName) {
        if (principal instanceof Collection) {
            this.addAll((Collection)principal, realmName);
        } else {
            this.add(principal, realmName);
        }
    }

    public RedisPrincipalCollection(Collection principals, String realmName) {
        this.addAll(principals, realmName);
    }

    public RedisPrincipalCollection(PrincipalCollection principals) {
        this.addAll(principals);
    }

    protected Collection getPrincipalsLazy(String realmName) {
        if (this.realmPrincipals == null) {
            this.realmPrincipals = new LinkedHashMap();
        }
        Set principals = (Set)this.realmPrincipals.get(realmName);
        if (principals == null) {
            principals = new LinkedHashSet();
            this.realmPrincipals.put(realmName, principals);
        }
        return (Collection)principals;
    }

    @Transient
    @Override
    public Object getPrimaryPrincipal() {
        return this.isEmpty() ? null : this.iterator().next();
    }

    @Override
    public void add(Object principal, String realmName) {
        if (realmName == null) {
            throw new NullPointerException("realmName argument cannot be null.");
        } else if (principal == null) {
            throw new NullPointerException("principal argument cannot be null.");
        } else {
            this.getPrincipalsLazy(realmName).add(principal);
        }
    }

    @Override
    public void addAll(Collection principals, String realmName) {
        if (realmName == null) {
            throw new NullPointerException("realmName argument cannot be null.");
        } else if (principals == null) {
            throw new NullPointerException("principals argument cannot be null.");
        } else if (principals.isEmpty()) {
            throw new IllegalArgumentException("principals argument cannot be an empty collection.");
        } else {
            this.getPrincipalsLazy(realmName).addAll(principals);
        }
    }

    @Override
    public void addAll(PrincipalCollection principals) {
        if (principals.getRealmNames() != null) {
            Iterator var2 = principals.getRealmNames().iterator();

            while(var2.hasNext()) {
                String realmName = (String)var2.next();
                Iterator var4 = principals.fromRealm(realmName).iterator();

                while(var4.hasNext()) {
                    Object principal = var4.next();
                    this.add(principal, realmName);
                }
            }
        }

    }

    @Override
    public <T> T oneByType(Class<T> type) {
        if (this.realmPrincipals != null && !this.realmPrincipals.isEmpty()) {
            Collection<Set> values = this.realmPrincipals.values();
            Iterator var3 = values.iterator();

            while(var3.hasNext()) {
                Set set = (Set)var3.next();
                Iterator var5 = set.iterator();

                while(var5.hasNext()) {
                    Object o = var5.next();
                    if (type.isAssignableFrom(o.getClass())) {
                        return (T)o;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    @Override
    public <T> Collection<T> byType(Class<T> type) {
        if (this.realmPrincipals != null && !this.realmPrincipals.isEmpty()) {
            Set<T> typed = new LinkedHashSet();
            Collection<Set> values = this.realmPrincipals.values();
            Iterator var4 = values.iterator();

            while(var4.hasNext()) {
                Set set = (Set)var4.next();
                Iterator var6 = set.iterator();

                while(var6.hasNext()) {
                    Object o = var6.next();
                    if (type.isAssignableFrom(o.getClass())) {
                        typed.add((T)o);
                    }
                }
            }

            if (typed.isEmpty()) {
                return Collections.EMPTY_SET;
            } else {
                return Collections.unmodifiableSet(typed);
            }
        } else {
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public List asList() {
        Set all = this.asSet();
        return all.isEmpty() ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList(all));
    }

    @Override
    public Set asSet() {
            if (this.realmPrincipals != null && !this.realmPrincipals.isEmpty()) {
            Set aggregated = new LinkedHashSet();
            Collection<Set> values = this.realmPrincipals.values();
            Iterator var3 = values.iterator();

            while(var3.hasNext()) {
                Set set = (Set)var3.next();
                aggregated.addAll(set);
            }

            return aggregated.isEmpty() ? Collections.EMPTY_SET : Collections.unmodifiableSet(aggregated);
        } else {
            return Collections.EMPTY_SET;
        }
    }

    @Override
    public Collection fromRealm(String realmName) {
        if (this.realmPrincipals != null && !this.realmPrincipals.isEmpty()) {
            Set principals = (Set)this.realmPrincipals.get(realmName);
            if (principals == null || principals.isEmpty()) {
                principals = Collections.EMPTY_SET;
            }

            return Collections.unmodifiableSet(principals);
        } else {
            return Collections.EMPTY_SET;
        }
    }

    @Transient
    @Override
    public Set<String> getRealmNames() {
        return this.realmPrincipals == null ? null : this.realmPrincipals.keySet();
    }

    @Transient
    @Override
    public boolean isEmpty() {
        return this.realmPrincipals == null || this.realmPrincipals.isEmpty();
    }

    @Override
    public void clear() {
        if (this.realmPrincipals != null) {
            this.realmPrincipals.clear();
            this.realmPrincipals = null;
        }

    }

    @Override
    public Iterator iterator() {
        return this.asSet().iterator();
    }

}
