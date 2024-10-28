import logging
import os
import sys
from datetime import datetime
from types import TracebackType
from typing import Type, Optional


def handleException(excType: Type[BaseException], excValue: BaseException, excTraceback: Optional[TracebackType],
                    message: str = "Uncaught Exception") -> None:
    """Function passed to python internals to handle exceptions by the logger"""
    if issubclass(excType, KeyboardInterrupt):
        logging.critical("Received keyboard interrupt.")
        sys.__excepthook__(excType, excValue, excTraceback)
    else:
        logging.critical(message, exc_info=(excType, excValue, excTraceback))


def setupLogging(outputDir: Optional[str] = None, debug: bool = False) -> None:
    """
    Sets up standard script logging
    :param verbosity:  1 for info, 2 for debug
    :param outputDir:  Folder to save log files, if none does not save
    :param outputName: Name of the log file, used as a prefix for the date
    :param args: If defined, dumps the arguments next to the namespace
    :return:  Date used for output files.
    """

    formatter = logging.Formatter("%(asctime)s [%(levelname)-5.5s] [%(filename)s]\t %(message)s")
    root = logging.getLogger()

    # always log to console
    consoleHandler = logging.StreamHandler(sys.stdout)
    consoleHandler.setFormatter(formatter)
    root.addHandler(consoleHandler)
    
    # apply logging level
    if debug:
        root.setLevel(logging.DEBUG)
    else:
        root.setLevel(logging.INFO)

    # add exception handler so those get logged
    sys.excepthook = handleException
    logging.captureWarnings(True)

    # optionally log to file
    date = datetime.now().strftime("%Y%m%d-%H%M%S")
    if outputDir is not None:
        os.makedirs(outputDir, exist_ok=True)
        logPath = os.path.join(outputDir, f"datagen-{date}.log")
        fileHandler = logging.FileHandler(logPath)
        fileHandler.setFormatter(formatter)
        root.addHandler(fileHandler)
        logging.info(f"Saving datagen logs to {logPath}")
    else:
        logging.info("Not saving datagen logs")