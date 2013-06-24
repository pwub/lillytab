/**
 * (c) 2009-2013 Peter Wullinger
 *
 * $Id$
 *
 * Use, modification and restribution of this file are covered by the terms of the Artistic License 2.0.
 *
 * You should have received a copy of the license terms in a file named "LICENSE" together with this software package.
 *
 * Disclaimer of Warranty: THE PACKAGE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS' AND WITHOUT ANY
 * EXPRESS OR IMPLIED WARRANTIES. THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT ARE DISCLAIMED TO THE EXTENT PERMITTED BY YOUR LOCAL LAW. UNLESS REQUIRED BY LAW, NO COPYRIGHT
 * HOLDER OR CONTRIBUTOR WILL BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY
 * WAY OUT OF THE USE OF THE PACKAGE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.dhke.projects.lutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * Class to be used as a base class for other classes performing logging operations.
 * <p />
 * This class automatically derives a logger name from the class instance it was created for or was provided via a
 * constructor parameter.
 * <p />
 * It provides various convenience methods to handle logging tasks.
 *
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class LoggingClass
{
	private final Logger _logger;

	private LoggingClass(final String loggerName)
	{
		_logger = LoggerFactory.getLogger(loggerName);
	}

	public LoggingClass(Class<?> destinationClass)
	{
		_logger = LoggerFactory.getLogger(destinationClass);
	}

	protected LoggingClass()
	{
		_logger = LoggerFactory.getLogger(getClass());
	}

	public Logger getLogger()
	{
		return _logger;
	}

	private StackTraceElement getExternalCaller(final StackTraceElement[] stackTrace)
	{
		int i = 1;
		while (stackTrace[i].getClassName().equals(LoggingClass.class.getCanonicalName())
			|| (stackTrace[i].getClass().getPackage().getName().startsWith("org.slf4j")))
			++i;
		return stackTrace[i];
	}

	private StackTraceElement getExternalCaller()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return getExternalCaller(stackTrace);
	}

	public void logError(final String fmt, final Object... args)
	{
		_logger.error(fmt, args);
	}

	public void logWarning(final String fmt, final Object... args)
	{
		_logger.warn(fmt, args);
	}

	public void logWarning(final Object obj)
	{
		if (_logger.isWarnEnabled())
			_logger.warn(obj.toString());
	}

	public void logInfo(final String fmt, final Object... args)
	{
		_logger.info(fmt, args);
	}

	public void logInfo(final Object obj)
	{
		if (_logger.isInfoEnabled())
			_logger.info(obj.toString());
	}

	public void logDebug(final String fmt, final Object... args)
	{
		_logger.debug(fmt, args);
	}

	public void logDebug(final Object obj)
	{
		if (_logger.isDebugEnabled())
			_logger.debug(obj.toString());
	}

	public void logTrace(final String fmt, final Object... args)
	{
		_logger.trace(fmt, args);;
	}

	public void logTrace(final Object obj)
	{
		if (_logger.isTraceEnabled())
			_logger.trace(obj.toString());
	}

	public void logThrowing(String sourceClass, String sourceMethod, Throwable throwable)
	{
		_logger.warn(String.format("%s:%s()", sourceClass, sourceMethod), throwable);
	}

	public void logThrowing(Throwable throwable)
	{
		if (_logger.isWarnEnabled()) {
			final StackTraceElement element = getExternalCaller(throwable.getStackTrace());
			logThrowing(element.getClassName(), element.getMethodName(), throwable);
		}
	}
}
