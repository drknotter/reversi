package com.drknotter.reversi.pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by plunkett on 2/28/15.
 */
public class BijectiveHashMap<Domain, Codomain>
{
    private Map<Domain, Codomain> _domainToCodomain = new ConcurrentHashMap<Domain, Codomain>();
    private Map<Codomain, Domain> _codomainToDomain = new ConcurrentHashMap<Codomain, Domain>();

    synchronized public boolean containsDomain(Domain elem)
    {
        return _domainToCodomain.containsKey(elem) && _codomainToDomain.containsValue(elem);
    }

    synchronized public boolean containsCodomain(Codomain elem)
    {
        return _codomainToDomain.containsKey(elem) && _domainToCodomain.containsValue(elem);
    }

    public Domain getDomain(Codomain codomElem)
    {
        return _codomainToDomain.get(codomElem);
    }

    public Codomain getCodomain(Domain domElem)
    {
        return _domainToCodomain.get(domElem);
    }

    public boolean isEmpty()
    {
        return _domainToCodomain.isEmpty() && _codomainToDomain.isEmpty();
    }

    synchronized public void put(Domain domElem, Codomain codomElem)
    {
        _domainToCodomain.put(domElem, codomElem);
        _codomainToDomain.put(codomElem, domElem);
    }

    public int size()
    {
        if (_domainToCodomain.size() == _codomainToDomain.size())
            return _domainToCodomain.size();
        return -1;
    }

    synchronized public Codomain removeByDomain(Domain domElem)
    {
        Codomain removed = _domainToCodomain.remove(domElem);
        _codomainToDomain.remove(removed);
        return removed;
    }

    synchronized public Domain removeByCodomain(Codomain codomElem)
    {
        Domain removed = _codomainToDomain.remove(codomElem);
        _domainToCodomain.remove(removed);
        return removed;
    }
}
