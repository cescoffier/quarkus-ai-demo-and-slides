package io.quarkus.presentation.ai.workflow;

import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.agent;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.emitJson;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.function;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.listen;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.switchWhenOrElse;
import static io.serverlessworkflow.fluent.func.dsl.FuncDSL.toOne;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkiverse.flow.Flow;
import io.serverlessworkflow.api.types.FlowDirectiveEnum;
import io.serverlessworkflow.api.types.Workflow;
import io.serverlessworkflow.fluent.func.FuncWorkflowBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RefundWorkflow extends Flow {

    @Inject
    RefundAnalysisAgent analysisAgent;

    @Inject
    RefundService refundService;

    @Override
    public Workflow descriptor() {
        return FuncWorkflowBuilder.workflow("refund-workflow")
                .tasks(
                        agent("analyzeRefund", analysisAgent::analyze, RefundRequest.class),
                        emitJson("needsApproval", "refund.approval.required", RefundAnalysis.class),
                        listen("waitDecision", toOne("refund.decision.made"))
                                .outputAs((JsonNode node) -> node.isArray() ? node.get(0) : node),
                        switchWhenOrElse(
                                d -> RefundDecision.Status.APPROVED.equals(d.status()),
                                "processRefund", "notifyRejection", RefundDecision.class),
                        function("processRefund", refundService::processRefund, RefundDecision.class)
                                .then(FlowDirectiveEnum.END),
                        function("notifyRejection", refundService::notifyRejection, RefundDecision.class)
                                .then(FlowDirectiveEnum.END))
                .build();
    }
}
