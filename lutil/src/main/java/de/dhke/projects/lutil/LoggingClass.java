/**
 * (c) 2009-2012 Peter Wullinger
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Class to be used as a base class for other classes performing logging operations.
 * </p><p>
 * This class automatically derives a logger name from the class instance it was created for or was provided via a
 * constructor parameter.
 * </p><p>
 * It provides various convenience methods to handle logging tasks.
 * </p>
 *
 * @author Peter Wullinger <java@dhke.de>
 */
public class LoggingClass {

	private final Logger _logger;


	private LoggingClass(final String loggerName)
	{
		_logger = Logger.getLogger(loggerName);
	}


	public LoggingClass(Class destinationClass)
	{
		this(destinationClass.getCanonicalName());
	}


	protected LoggingClass()
	{
		_logger = Logger.getLogger(getClass().getCanonicalName());
	}


	public Logger getLogger()
	{
		return _logger;
	}


	private StackTraceElement getExternalCaller(final StackTraceElement[] stackTrace)
	{
		int i = 1;
		while (stackTrace[i].getClassName().equals(LoggingClass.class.getCanonicalName()))
			++i;
		return stackTrace[i];
	}


	private StackTraceElement getExternalCaller()
	{
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return getExternalCaller(stackTrace);
	}


	public void logFormat(final Logger logger, final Level level, final String fmt, final Object... args)
	{
		if (logger.isLoggable(level)) {
			final StackTraceElement element = getExternalCaller();
			logger.logp(level, element.getClassName(), element.getMethodName(), String.format(fmt, args));
		}
	}


	public void logFormat(final Level level, final String fmt, final Object... args)
	{
		logFormat(getLogger(), level, fmt, args);
	}


	public void log(final Logger logger, final Level level, final Object obj)
	{
		if (logger.isLoggable(level)) {
			final StackTraceElement element = getExternalCaller();
			logger.logp(level, element.getClassName(), element.getMethodName(), obj.toString());
		}
	}


	public void log(final Level level, final Object obj)
	{
		log(getLogger(), level, obj);
	}


	public void logSevere(final Logger logger, final String fmt, final Object... args)
	{
		logFormat(logger, Level.SEVERE, fmt, args);
	}


	public void logSevere(final Logger logger, final Object obj)
	{
		if (logger.isLoggable(Level.SEVERE))
			log(logger, Level.SEVERE, obj);
	}


	public void logSevere(final String fmt, final Object... args)
	{
		logFormat(Level.SEVERE, fmt, args);
	}


	public void logWarning(final Logger logger, String fmt, final Object... args)
	{
		logFormat(logger, Level.WARNING, fmt, args);
	}


	public void logWarning(final String fmt, final Object... args)
	{
		logFormat(Level.WARNING, fmt, args);
	}


	public void logWarning(final Object obj)
	{
		log(Level.WARNING, obj);
	}


	public void logInfo(final Logger logger, final String fmt, final Object... args)
	{
		logFormat(logger, Level.INFO, fmt, args);
	}


	public void logInfo(final String fmt, final Object... args)
	{
		logFormat(Level.INFO, fmt, args);
	}


	public void logInfo(final Object obj)
	{
		log(Level.INFO, obj);
	}


	public void logFine(final Logger logger, final String fmt, final Object... args)
	{
		logFormat(logger, Level.FINE, fmt, args);
	}


	public void logFine(final String fmt, final Object... args)
	{
		logFormat(Level.FINE, fmt, args);
	}


	public void logFine(final Object obj)
	{
		log(Level.FINE, obj);
	}


	public void logFiner(final Logger logger, final String fmt, final Object... args)
	{
		logFormat(logger, Level.FINER, fmt, args);
	}


	public void logFiner(final String fmt, final Object... args)
	{
		logFormat(Level.FINER, fmt, args);
	}


	public void logFiner(final Object obj)
	{
		log(Level.FINER, obj);
	}


	public void logFinest(final Logger logger, final String fmt, final Object... args)
	{
		logFormat(logger, Level.FINEST, fmt, args);
	}


	public void logFinest(final String fmt, final Object... args)
	{
		logFormat(Level.FINEST, fmt, args);
	}


	public void logFinest(final Object obj)
	{
		log(Level.FINEST, obj);
	}


	public void logThrowing(String sourceClass, String sourceMethod, Throwable throwable)
	{
		getLogger().throwing(sourceClass, sourceMethod, throwable);
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		pw.println(throwable.getMessage());
		throwable.printStackTrace(pw);
		pw.flush();

		if (throwable.getCause() != null)
			logThrowing(sourceClass, sourceMethod, throwable.getCause());

		getLogger().logp(Level.SEVERE, sourceClass, sourceMethod, sw.getBuffer().toString());
	}


	public void logThrowing(Throwable throwable)
	{
		final StackTraceElement element = getExternalCaller(throwable.getStackTrace());
		logThrowing(element.getClassName(), element.getMethodName(), throwable);

	}
}
