package idlreasonerchoco.analyzer.operations.oas;

import org.apache.log4j.Logger;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;

import idlreasonerchoco.configuration.ErrorType;
import idlreasonerchoco.configuration.IDLException;
import idlreasonerchoco.mapper.OASMapper;
import idlreasonerchoco.utils.ExceptionManager;
import idlreasonerchoco.utils.Utils;

public class OASDeadParameter implements AnalysisOperation {

    private static final Logger LOG = Logger.getLogger(OASDeadParameter.class);

    private final OASMapper mapper;
    private final String paramName;

    public OASDeadParameter(OASMapper mapper, String paramName) {
        this.mapper = mapper;
        this.paramName = paramName;
    }

    public boolean analyze() throws IDLException {
        if (mapper.getVariablesMap().get(Utils.parseIDLParamName(paramName) + "Set") != null) {
        	BoolVar varSet = mapper.getVariablesMap().get(Utils.parseIDLParamName(paramName) + "Set").asBoolVar();
            Constraint cons = mapper.getChocoModel().arithm(varSet, "=", 1);
            cons.post();
            mapper.getChocoModel().getSolver().reset();
            boolean result = !mapper.getChocoModel().getSolver().solve();
            mapper.getChocoModel().unpost(cons);
            mapper.getChocoModel().getSolver().reset();
            return result;
        } else {
            ExceptionManager.rethrow(LOG, ErrorType.ERROR_OPERATION_PARAM.toString());
            return false;
        }
    }
}
