PYTHON_BASEVERSION ?= "2.7"
PYTHON_DIR = "python${PYTHON_BASEVERSION}"
PYTHON_PN = "python${@'' if '${PYTHON_BASEVERSION}'.startswith('2') else '3'}"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/site-packages"
