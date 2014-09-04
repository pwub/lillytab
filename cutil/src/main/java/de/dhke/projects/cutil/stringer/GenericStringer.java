/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dhke.projects.cutil.stringer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


/**
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class GenericStringer
	extends AbstractToStringConverter
{
	static final IToStringConverter DEFAULT_CONVERTER = new SimpleToStringConverter();
	private final boolean _doCache;

	private final Set<Class<?>> _cacheable = new HashSet<>();
	private final Set<Class<?>> _uncacheable = new HashSet<>();
	private final Map<Object, String> _cache = new WeakHashMap<>();

	private final Map<Class<?>, IToStringConverter> _stringerMap = new LinkedHashMap<>();
	private final List<IToStringConverter> _stringers = new ArrayList<>();

	public GenericStringer(final boolean doCache)
	{
		_doCache = doCache;
	}

	public boolean isCacheable(final Object obj)
	{
		if (_doCache) {
			final Class<?> klass = obj.getClass();
			if (_cacheable.contains(klass)) {
				return true;
			} else if (_uncacheable.contains(klass)) {
				return false;
			} else {
				for (Class<?> cacheableClass : _cacheable) {
					if (klass.isAssignableFrom(cacheableClass)) {
						_cacheable.add(klass);
						return true;
					}
				}
				_uncacheable.add(klass);
				return false;
			}
		} else {
			return false;
		}
	}

	public Set<Class<?>> getCacheables()
	{
		return _cacheable;
	}

	public void registerStringer(IToStringConverter stringer)
	{
		_stringers.add(stringer);
		_stringerMap.clear();
	}

	@Override
	public boolean canHandle(final Class<?> objectType)
	{
		return true;
	}

	@Override
	public void append(StringBuilder sb, Object obj)
	{
		append(sb, obj, this);
	}

	@Override
	public void append(StringBuilder sb, Object obj, IToStringConverter backStringer)
	{
		if (obj == null) {
			sb.append("(null)");
		} else {
			if (_doCache && _cache.containsKey(obj)) {
				sb.append(_cache.get(obj));
			} else {
				final IToStringConverter converter = findStringer(obj);
				if (_doCache && (isCacheable(obj))) {
					final String strVal = converter.toString(obj);
					_cache.put(obj, strVal);
					sb.append(strVal);
				} else {
					converter.append(sb, obj, backStringer);
				}
			}
		}
	}

	private IToStringConverter findStringer(final Object obj)
	{
		if (obj == null)
			return this;

		final Class<?> objectType = obj.getClass();
		IToStringConverter converter = _stringerMap.get(objectType);
		if (converter == null) {
			for (IToStringConverter stringer : _stringers) {
				if (stringer.canHandle(objectType)) {
					_stringerMap.put(objectType, stringer);
					converter = stringer;
					break;
				}
			}
		}
		if (converter == null)
			converter = DEFAULT_CONVERTER;
		return converter;
	}
}
